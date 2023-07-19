## Object Parsing

### Comparing different techniques of finding a value through a path string and return the result

Parsers tested:

1) Jayway JSONPath
2) Java reference implementation of JSON Processing (Glassfish JSONP)
3) A plain old Java Map with recursion

The goal was to investigate different ways and the performance of each way to determine which is the faster parser. The
path to the field / key would look something like ```field1.subField1.<array position>.finalField``` and variants of the
same in their own implementations.

It isn't a surprise that the Map implementation is faster than anything else. Results measured with ```StopWatch``` from
Spring in a JUnit test:

| nanoseconds | % of time | Test name     |
|-------------|-----------|---------------|
| 15803083    | 62%       | JsonPath      |
| 7270000     | 28%       | JSONP         |
| 1111375     | 4%        | Map Recursion |
| 21375       | 0%        | Map Recursion |
| 22416       | 0%        | Map Recursion |
| 1307334     | 5%        | Map Recursion |
| 23417       | 0%        | Map Recursion |