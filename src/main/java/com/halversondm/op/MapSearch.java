package com.halversondm.op;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapSearch {

    /**
     * A method to recursively find the final value in a path trail that was divided coming into the method by its separator
     *
     * @param payload          - Starting with a `Map<String, Object>` and moving into each successive response with an Object of different types such as List or String
     * @param fieldToFindArray - A pre-separated list of key names that trails down to the ultimate location
     * @return - likely a String or int of some sort
     */
    public static Object find(Object payload, String[] fieldToFindArray) {

        if (fieldToFindArray.length == 0 || payload == null) {
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
}
