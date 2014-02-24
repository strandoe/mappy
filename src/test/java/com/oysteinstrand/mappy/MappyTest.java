package com.oysteinstrand.mappy;

import com.google.common.base.Optional;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.oysteinstrand.mappy.Keys.keys;
import static com.oysteinstrand.mappy.Mappy.*;
import static com.oysteinstrand.mappy.MatchesTuples.matchesTuples;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class MappyTest {
    @Test
    public void should_pick_from_maps() throws Exception {
        Map<String, Object> map = Mappy.<Object>map(tuple("key1", "val1"), tuple("key2", "val2"), tuple("key3", map(tuple("key3A", "val3A"))));
        boolean isCorrectValue = pick(map, keys("key3.key3A")).equals("val3A");
        assertTrue(isCorrectValue);
    }

    @Test
    public void should_copy_maps_and_insert_new_tuples() throws Exception {
        Map<String, Object> map1 = map(tuple("hei", "hallo"));
        Map<String, Object> map2 = mapCopy(map1, tuple("hallo", "hei"));
        assertFalse(map1.equals(map2));
        assertTrue(map2.containsKey("hallo"));
    }

    @Test
    public void should_handle_list_patterns() throws Exception {
        Map<String, Object> map = Mappy.<Object>map(
                tuple("key1", "val1"),
                tuple("key2", "val2"),
                tuple("key3", map(tuple("key3A", asList(
                        map(tuple("hei", "hallo")),
                        map(tuple("hallo", "hei")))))
                )
        );
        assertEquals("hallo", pick(map, keys("key3.key3A[0].hei")));
        assertEquals("hei", pick(map, keys("key3.key3A[1].hallo")));
    }

    @Test
    public void should_pick_an_entire_list() throws Exception {
        List<Map<String, Object>> list = asList(
                map(tuple("hei", "hallo")),
                map(tuple("hallo", "hei")));

        Map<String, Object> map = Mappy.<Object>map(
                tuple("key1", "val1"),
                tuple("key2", "val2"),
                tuple("key3", map(tuple("key3A", list))
                )
        );
        assertEquals(list, pick(map, keys("key3.key3A")));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_throw_exception_when_asking_for_list_value_with_index_out_of_bounds() throws Exception {
        Map<String, Object> map = Mappy.<Object>map(
                tuple("key1", "val1"),
                tuple("key2", "val2"),
                tuple("key3", map(tuple("key3A", asList(
                        map(tuple("hei", "hallo")),
                        map(tuple("hallo", "hei")))))
                )
        );
        pick(map, keys("key3.key3A[2].hallo"));
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_when_asking_for_list_value_when_value_is_not_a_list() throws Exception {
        Map<String, Object> map = Mappy.<Object>map(
                tuple("key1", "val1"),
                tuple("key2", "val2"),
                tuple("key3", map(tuple("key3A", map(tuple("hei", "hallo"))))
                )
        );
        pick(map, keys("key3.key3A[0].hei"));
    }

    @Test
    public void should_pluck() throws Exception {
        List<Map<String, Object>> list = Arrays.asList(
                map(tuple("name", "Øystein"), tuple("address", "Pilestredet")),
                map(tuple("name", "Jimi Hendrix"), tuple("address", "The moon")));
        List values = pluck(list, "name");
        assertTrue(values.contains("Øystein"));
        assertTrue(values.contains("Jimi Hendrix"));
    }

    @Test
    public void should_find_where() throws Exception {
        List<Map<String, Object>> list = asList(
                map(tuple("name", "Øystein"), tuple("address", "Pilestredet")),
                null,
                map(tuple("name", "Jimi Hendrix"), tuple("address", "The moon")),
                map(tuple("name", "Christina"), tuple("address", "Pilestredet")),
                null,
                map(tuple("name", null), tuple("address", null)),
                map(tuple(null, null), tuple(null, null))
        );
        Optional<Map> found = findWhere(list, matchesTuples(tuple("address", "Pilestredet")));
        assertTrue(found.isPresent());
        assertTrue(found.get().equals(map(tuple("name", "Øystein"), tuple("address", "Pilestredet"))));
    }

    @Test(expected = NullPointerException.class)
    public void find_where_should_fail_on_null_list() {
        findWhere(null, null);
    }

    @Test
    public void find_where_should_handle_missing_predicate_keys() throws Exception {
        List<Map<String, Object>> list = asList(
                map(tuple("addresses", map(tuple("foo", "foo")))),
                map(tuple("addresses", map(tuple("foo", "foo")))));
        assertFalse(findWhere(list, matchesTuples(keys("addresses"), tuple("bar", "bar"))).isPresent());
    }

    @Test
    public void should_find_all() throws Exception {
        Map<String, Object> oystein = Mappy.<Object>map(
                tuple("name", "Øystein"),
                tuple("address", map(
                        tuple("countryCode", map(
                                tuple("NO", map(
                                        tuple("street", "Pilestredet")
                                ))))))
        );

        Map<String, Object> jimi = Mappy.<Object>map(
                tuple("name", "Jimi Hendrix"),
                tuple("address", map(
                        tuple("countryCode", map(
                                tuple("NO", map(
                                        tuple("street", "The Moon")
                                ))))))
        );

        Map<String, Object> nobody = Mappy.<Object>map(
                tuple("name", "Jimi Hendrix")
        );

        Map<String, Object> christina = Mappy.<Object>map(
                tuple("name", "Christina"),
                tuple("address", map(
                        tuple("countryCode", map(
                                tuple("NO", map(
                                        tuple("street", "Pilestredet")
                                ))))))
        );

        List<Map<String, Object>> list = asList(oystein, nobody, jimi, christina);

        Iterable found = filter(list, matchesTuples(keys("address.countryCode.NO"), tuple("street", "Pilestredet")));
        assertTrue(contains(found, oystein));
        assertTrue(contains(found, christina));
        assertFalse(contains(found, jimi));
        assertFalse(contains(found, nobody));
    }

    @Test
    public void should_search_in_maps_with_lists() throws Exception {
        Iterable found = where(ObjectMother.countries, keys("countries.nordic"), tuple("countryCode", "SE"));
        assertTrue(contains(found, ObjectMother.sweden));
    }

    @Test
    public void should_find_where_multiple_conditions() throws Exception {
        List<Map<String, Object>> list = Arrays.asList(
                map(tuple("name", "Øystein"), tuple("address", "Pilestredet")),
                null,
                map(tuple("name", "Jimi Hendrix"), tuple("address", "The moon")),
                map(tuple("name", "Christina"), tuple("address", "Pilestredet")),
                null,
                map(tuple("name", null), tuple("address", null)),
                map(tuple(null, null), tuple(null, null))
        );
        Optional<Map> found = findWhere(list, matchesTuples(tuple("name", "Stian"), tuple("address", "Pilestredet")));
        assertFalse(found.isPresent());
    }
}
