package com.halversondm.op;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONTest {

    private static final Logger LOGGER = LogManager.getLogger(JSONTest.class);

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

    @Test
    void testFind() throws IOException {
        // assume this was done for you coming into the controller method
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payload = objectMapper.readValue(json, Map.class);
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
        LOGGER.info("Testing path '{}' with expected result of '{}'", fieldToFind, expectedResult);
        String[] fieldToFindArray = fieldToFind.split("\\.");
        try {
            assertEquals(expectedResult, find(payload, fieldToFindArray));
        } catch (RuntimeException runtimeException) {
            LOGGER.error("", runtimeException);
        }
    }


    // method under test
    private Object find(Object payload, String[] fieldToFindArray) {

        if (fieldToFindArray.length == 0) {
            return payload;
        }

        if (payload instanceof Map) {
            Object value = ((Map<?, ?>) payload).size() > 0 ? ((Map<?, ?>) payload).get(fieldToFindArray[0]) : null;
            if (value != null) {
                String[] newFieldToFindArray = Arrays.copyOfRange(fieldToFindArray, 1, fieldToFindArray.length);
                return find(value, newFieldToFindArray);
            } else {
                throw new RuntimeException("Key " + fieldToFindArray[0] + " is not found");
            }
        } else if (payload instanceof List) {
            try {
                int position = Integer.parseInt(fieldToFindArray[0]);
                Object value = ((List<?>) payload).size() > 0 ? ((List<?>) payload).get(position) : null;
                if (value != null) {
                    String[] newFieldToFindArray = Arrays.copyOfRange(fieldToFindArray, 1, fieldToFindArray.length);
                    return find(value, newFieldToFindArray);
                } else {
                    throw new RuntimeException("Key " + fieldToFindArray[0] + " is not found");
                }
            } catch (NumberFormatException numberFormatException) {
                throw new RuntimeException("Key " + fieldToFindArray[0] + " is not a number to find in a list");
            }
        }

        return payload;
    }

    @Test
    void testJSONP() {

        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonStructure jsonStructure = jsonReader.read();
        JsonValue jsonValue = jsonStructure.getValue("/phoneNumbers/0/number");
        LOGGER.info("{}", jsonValue);
        jsonReader.close();
    }
}
