package com.halversondm.op;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class JSONTest {

    static StopWatch stopWatch = new StopWatch();

    String json = """
            {
                "firstName": "John",
                "lastName": "Smith",
                "age": 25,
                "address": {
                    "streetAddress": "21 2nd Street",
                    "city": "New York",
                    "state": "NY",
                    "postalCode": 10021
                },
                "phoneNumbers": [
                    {
                        "type": "home",
                        "number": "212 555-1234"
                    },
                    {
                        "type": "fax",
                        "number": "646 555-4567"
                    }
                ],
                "hobbies": [],
                "interests": ["",""]
            }""";

    @AfterAll
    static void logging() {
        log.info("{}", stopWatch.prettyPrint());
    }

    @Test
    void testFind() throws IOException {
        // assume this was done for you coming into the controller method
        Map<String, Object> payload = (Map<String, Object>) commonTest("Initial Mapping for Map Recursion", (tag) -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, Map.class);
            } catch (Exception e) {
                log.error("", e);
            }
            return null;
        });

        //

        // this is the path string that we need to find in the original structure
        testCase("phoneNumbers.0.number", payload, "212 555-1234");
        testCase("firstName", payload, "John");
        testCase("interests.1", payload, "");
        testCase("hobbies.0", payload, null);
        testCase("age", payload, 25);

    }

    private void testCase(String fieldToFind, Map<String, Object> payload, Object expectedResult) {
        // convert to an array using the separator to iterate through.
        Object result = commonTest("Map Recursion", (tag) -> {
            log.info("tag: {} Testing path '{}' with expected result of '{}'", tag, fieldToFind, expectedResult);
            String[] fieldToFindArray = fieldToFind.split("\\.");
            try {
                Object actualResult = MapSearch.find(payload, fieldToFindArray);
                assertEquals(expectedResult, actualResult);
                return actualResult;
            } catch (RuntimeException runtimeException) {
                log.error("", runtimeException);
            }
            return null;
        });
    }

    @Test
    void testJSONP() {
        Object jsonValue = commonTest("JSONP", (tag) -> {
            JsonReader jsonReader = Json.createReader(new StringReader(json));
            JsonStructure jsonStructure = jsonReader.read();
            return jsonStructure.getValue("/phoneNumbers/0/number");
        });
        assertNotNull(jsonValue);
    }

    @Test
    void testJsonPath() {
        Object read = commonTest("JsonPath", (tag) -> {
            String searchPath = "$.phoneNumbers[0].number";
            Configuration configuration = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
            DocumentContext documentContext = JsonPath.using(configuration).parse(json);
            return documentContext.read(searchPath);
        });
        assertNotNull(read);
    }

    Object commonTest(String tag, Function<String, Object> supplier) {
        stopWatch.start(tag);

        Object result = supplier.apply(tag);

        stopWatch.stop();
        log.info("tag: {} result: {}", tag, result);
        return result;
    }
}
