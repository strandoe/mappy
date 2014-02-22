package com.oysteinstrand.mappy;

import java.util.Map;

import static com.oysteinstrand.mappy.Mappy.map;
import static com.oysteinstrand.mappy.Mappy.tuple;
import static java.util.Arrays.asList;

@SuppressWarnings("unchecked")
public class ObjectMother {
    public static final Map<String, Object> countries;
    public static final Map<String, Object> sweden;
    public static final Map<String, Object> norway;
    public static final Map<String, Object> denmark;

    static {
        sweden = map(
                tuple("countryCode", "SE"),
                tuple("name", "Sweden"));
        norway = map(
                tuple("countryCode", "NO"),
                tuple("name", "Norway"));
        denmark = map(
                tuple("countryCode", "DK"),
                tuple("name", "Denmark"));

        countries = map(tuple("countries", map(tuple("nordic", asList(norway, sweden, denmark)))));
    }
}
