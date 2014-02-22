package com.oysteinstrand.mappy;

import java.util.List;

import static com.google.common.collect.Iterables.toArray;
import static com.oysteinstrand.mappy.Utils.keysFromPattern;
import static java.util.Arrays.asList;

public class Keys {
    public final List<String> keys;

    private Keys(List<String> keys) {
        this.keys = keys;
    }

    public static Keys keys(String keyPattern) {
        return new Keys(asList(toArray(keysFromPattern(keyPattern), String.class)));
    }

    public static Keys keys(List<String> keys) {
        return new Keys(keys);
    }

    public static Keys keys(String ... keys) {
        return new Keys(asList(keys));
    }

    public static Keys keys(Iterable<String> keys) {
        return keys(toArray(keys, String.class));
    }
}
