package com.oysteinstrand.mappy;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.oysteinstrand.mappy.MatchesTuples.matchesTuples;

@SuppressWarnings("unchecked")
public class Mappy {
    private Mappy(){}

    public static Tuple tuple(String k, Object v) {
        return new Tuple(k,v);
    }

    public static Map<String, Object> map(Tuple ... tuples){
        HashMap<String, Object> m = new HashMap<String, Object>();
        for (Tuple tuple : tuples) {
            m.put(tuple.k, tuple.v);
        }
        return m;
    }

    public static Map<String, Object> mapCopy(Map<String, Object> originalMap, Tuple ... tuples) {
        Map<String, Object> copy = new HashMap<String, Object>();
        copy.putAll(originalMap);
        for (Tuple tuple : tuples) {
            copy.put(tuple.k, tuple.v);
        }
        return copy;
    }

    public static <T> T pick(Map<String, Object> map, Keys keys, Class<T> type) {
        return type.cast(pick(map, keys));
    }

    public static Object pick(Map<String, Object> map, Keys keys) {
        return pickInternal(map, keys.keys, new ArrayList<String>(), 0);
    }

    public static Object pickInternal(Map<String, Object> map, List<String> keys, List<String> visitedKeys, int index) {
        if (index > keys.size() - 1 || map == null) {
            return null;
        }
        String currentKey = keys.get(index);
        visitedKeys.add(currentKey);
        Matcher arrayWithIndexMatcher = Pattern.compile("(\\w+)\\[(\\d)\\]").matcher(currentKey);

        Object o;
        if (arrayWithIndexMatcher.find() && arrayWithIndexMatcher.groupCount() > 1) {
            String kkey = arrayWithIndexMatcher.group(1);
            int i = Integer.valueOf(arrayWithIndexMatcher.group(2));
            Object val = map.get(kkey);
            if (val == null || !(val instanceof List)) {
                throw new IllegalStateException("Expected list at " + Joiner.on(".").join(visitedKeys));
            }
            List list = (List) val;
            if (i > list.size() - 1) {
                throw new IndexOutOfBoundsException("List at " + Joiner.on(".").join(visitedKeys) + " was larger than index " + i + " you asked for");
            }
            o = list.get(i);
        }
        else {
            o = map.get(currentKey);
        }

        if (!(o instanceof Map) || index + 1 > keys.size() - 1) {
            return o;
        }
        return pickInternal((Map)o, keys, visitedKeys, index + 1);
    }

    public static <V> List<V> pluck(List<Map<String, V>> list, String propertyName) {
        List<V> values = new ArrayList<V>();
        for (Map<String, V> m : list) {
            values.add(m.get(propertyName));
        }
        return values;
    }

    public static Optional<Map> findWhere(List<Map<String, Object>> list, Predicate<Map<String, Object>> predicate) {
        Map match;
        try {
            match = Iterators.find(list.iterator(), predicate);
        } catch (NoSuchElementException e) {
            return Optional.absent();
        }
        return Optional.of(match);
    }

    public static Iterable<Map<String, Object>> where(Map<String, Object> map, Keys keys, final Tuple ... tuples) {
        Iterable<Map<String, Object>> found = (Iterable<Map<String, Object>>) pick(map, keys);
        return Iterables.filter(found, matchesTuples(tuples));
    }
}
