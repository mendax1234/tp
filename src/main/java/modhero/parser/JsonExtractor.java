package modhero.parser;

/**
 * Extract values from raw JSON strings.
 */
public class JsonExtractor {

    /**
     * Extracts the value associated with a given key from a JSON string.
     * Handles both JSON objects (enclosed in {}) and string/primitive values.
     *
     * @param json The JSON string to parse.
     * @param dataType The key name to search for in the JSON.
     * @return The extracted value as a string, or null if the key is not found.
     */
    public String getArg(String json, String dataType) {
        int keyIndex = findKeyIndex(json, dataType);
        if (keyIndex == -1) {
            return null;
        }

        int valueStart = findValueStart(json, keyIndex);
        char firstChar = json.charAt(valueStart);

        if (firstChar == '{') {
            return extractJsonObject(json, valueStart);
        } else if (firstChar == '\"') {
            return extractJsonString(json, valueStart);
        } else {
            return extractRawValue(json, valueStart);
        }
    }

    /**
     * Finds the index of a given key in a JSON string.
     *
     * @param json The JSON string to search in.
     * @param key The key name to find.
     * @return The index of the key, or -1 if not found.
     */
    private int findKeyIndex(String json, String key) {
        return json.indexOf('\"' + key + '\"');
    }

    /**
     * Finds the starting index of the value associated with a key in JSON.
     * Skips whitespace after the colon separator.
     *
     * @param json The JSON string.
     * @param keyIndex The index where the key was found.
     * @return The index where the value starts.
     */
    private int findValueStart(String json, int keyIndex) {
        int colonIdx = json.indexOf(':', keyIndex);
        int idx = colonIdx + 1;
        while (idx < json.length() && Character.isWhitespace(json.charAt(idx))) {
            idx++;
        }
        return idx;
    }

    /**
     * Extracts a JSON object (enclosed in curly braces) from a JSON string.
     * Handles nested objects by counting opening and closing braces.
     *
     * @param json The JSON string.
     * @param startIdx The index where the object starts (position of opening brace).
     * @return The extracted JSON object as a string, including the braces, or null if malformed.
     */
    private String extractJsonObject(String json, int startIdx) {
        int braceCount = 0;
        for (int i = startIdx; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    return json.substring(startIdx, i + 1);
                }
            }
        }
        return null;
    }

    /**
     * Extracts a string value (enclosed in quotes) from a JSON string.
     *
     * @param json The JSON string.
     * @param startIdx The index where the opening quote is located.
     * @return The extracted string value without the surrounding quotes.
     */
    private String extractJsonString(String json, int startIdx) {
        int quoteStart = startIdx + 1;
        int quoteEnd = json.indexOf('\"', quoteStart);
        return json.substring(quoteStart, quoteEnd);
    }

    /**
     * Extracts a raw value (number, boolean, or null) from a JSON string.
     *
     * @param json The JSON string.
     * @param startIdx The index where the raw value starts.
     * @return The extracted raw value as a string.
     */
    private String extractRawValue(String json, int startIdx) {
        int endIdx = startIdx;
        while (endIdx < json.length() &&
                (Character.isLetterOrDigit(json.charAt(endIdx))
                        || json.charAt(endIdx) == '.'
                        || json.charAt(endIdx) == '-')) {
            endIdx++;
        }
        return json.substring(startIdx, endIdx);
    }
}
