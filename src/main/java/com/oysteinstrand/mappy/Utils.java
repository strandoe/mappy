package com.oysteinstrand.mappy;

import com.google.common.base.Splitter;

public class Utils {
    public static Iterable<String> keysFromPattern(String keyPattern) {
        return Splitter.on(".")
                .trimResults()
                .omitEmptyStrings()
                .split(keyPattern);
    }
}
