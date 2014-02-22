package com.oysteinstrand.mappy;

import com.google.common.base.Predicate;

import java.util.Collections;
import java.util.Map;

import static com.oysteinstrand.mappy.Keys.keys;
import static com.oysteinstrand.mappy.Mappy.pick;

public class MatchesTuples implements Predicate<Map<String, Object>> {

    public final Iterable<String> keys;
    public final Tuple[] tuples;

    private MatchesTuples(Iterable<String> keys, Tuple... tuples) {
        this.keys = keys;
        this.tuples = tuples;
    }

    @Override
    public boolean apply(Map<String, Object> map) {
        Object o = keys.iterator().hasNext() ? pick(map, keys(keys)) : map;
        if (o == null || !(o instanceof Map)) {
            return false;
        }
        Map m = (Map)o;
        boolean matches = false;
        for (Tuple tuple : tuples) {
            matches = m.get(tuple.k) != null && m.get(tuple.k).equals(tuple.v);
        }
        return matches;
    }

    public static MatchesTuples matchesTuples(Tuple ... tuples) {
        return new MatchesTuples(Collections.<String>emptyList(), tuples);
    }

    public static MatchesTuples matchesTuples(Keys keys, Tuple ... tuples) {
        return new MatchesTuples(keys.keys, tuples);
    }
}
