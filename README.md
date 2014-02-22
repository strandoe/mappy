Mappy
=====

Mappy is `java.util.Map<String, Object> made simple. Mappy is useful when you are dealing with "maps of maps", e.g.
responses from JSON-API's. It is assumed that all keys are strings, but the values can be anything, particularly lists and other maps.

# Features

## Creating maps
- Simple creation of maps using tuples (key value pairs).
- Create a copy of a map and add in more tuples.

```java
Map<String, Object> map = map(tuple("key1", "val1"), tuple("key2", "val2"), tuple("key3", map(tuple("key3A", "val3A"))));
```

```java
Map<String, Object> map1 = map(tuple("hi", "hello"));
Map<String, Object> map2 = mapCopy(map1, tuple("hello", "hi"));
assertFalse(map1.equals(map2));
assertTrue(map2.containsKey("hello"));
```

## Fetch submaps, objects and properties of a map or list of maps
- Traverse a map using a key pattern like "countries.nordic[2].countryCode", and fetch the object at the last key in the key pattern.
- Pluck values from a list of maps, i.e. get the value for a key for all the maps in the list.

### Pluck properties

```java
List<Map<String, Object>> list = Arrays.asList(
                map(tuple("name", "Øystein"), tuple("address", "Pilestredet")),
                map(tuple("name", "Jimi Hendrix"), tuple("address", "The moon")));
List names = pluck(list, "name");
assertTrue(values.contains("Øystein"));
assertTrue(values.contains("Jimi Hendrix"));
```

### Pick a value or list

```java
Map<String, Object> map = Mappy.<Object>map(
                tuple("key1", "val1"),
                tuple("key2", "val2"),
                tuple("key3", map(tuple("key3A", asList(
                        map(tuple("hi", "hello")),
                        map(tuple("hello", "hi")))))
                )
        );
assertEquals("hello", pick(map, keys("key3.key3A[0].hi")));
assertEquals("hi", pick(map, keys("key3.key3A[1].hello")));
```

```java
List<Map<String, Object>> list = asList(
                map(tuple("hi", "hello")),
                map(tuple("hello", "hi")));

Map<String, Object> map = Mappy.<Object>map(
        tuple("key1", "val1"),
        tuple("key2", "val2"),
        tuple("key3", map(tuple("key3A", list))
        )
);
assertEquals(list, pick(map, keys("key3.key3A[]")));
```

## Finding submaps
Find the first or all maps located at a key pattern in a map or list of maps you are searching in, that
matches a list of tuples. The submaps match the tuples if they contain the same keys and values of the specified tuples.

### Finding in a list of maps at key pattern

```java
List<Map<String, Object>> list = asList(john, nobody, jimi, christina);
Iterable found = filter(list, matchesTuples(keys("address.countryCode.NO"), tuple("street", "12A End of the road")));
assertTrue(contains(found, christina));
```

### Finding in a map at key pattern

```java
Iterable found = where(ObjectMother.countries, keys("countries.nordic[]"), tuple("countryCode", "SE"));
assertTrue(contains(found, ObjectMother.sweden));
```

### Find the first match in a list of maps

```java
Optional<Map> found = findWhere(aListOfMaps, matchesTuples(keys("key1.key2.listKey[2].key4), tuple("name", "Jimi")));
boolean didFind = found.isPresent();
Map theFind = found.get();
```
