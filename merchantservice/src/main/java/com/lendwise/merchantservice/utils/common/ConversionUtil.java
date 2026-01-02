package com.lendwise.merchantservice.utils.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConversionUtil {

    public static final String ORG_POSTGRESQL_UTIL_PGOBJECT = "org.postgresql.util.PGobject";
    public static final String GET_TYPE = "getType";
    public static final String GET_VALUE = "getValue";
    public static final String JSONB = "jsonb";
    public static final String CLASS_NAME;
    public static final String INPUT_DATA_IS_EMPTY = "Input data is empty.";
    public static final String UNEXPECTED_ERROR_DURING_CONVERSION = "Unexpected error during conversion.";
    public static final String FAILED_TO_CONVERT_PGOBJECT_DATA = "Failed to convert PGObject data.";
    public static final String FAILED_TO_CONVERT_JSONB_FIELD = "Failed to convert JSONB field.";
    public static final String FAILED_TO_CONVERT_JSONB_FIELD_IN_LIST = "Failed to convert JSONB field in list.";

    static {
        CLASS_NAME = ConversionUtil.class.getName();
    }

    private ConversionUtil() {}
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    public static final String EMPTY_INPUT_DATA = "Empty data response";
    public static final String FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE = "Failed to convert input data.";


    /**
     * Convert Data string response to specified type
     * Useful while converting data from redis or db to any format desired
     * @param inputData Raw string data from Redis
     * @param typeReference TypeReference for the target conversion type
     * @param className Calling class name (for logging)
     * @param methodName Calling method name (for logging)
     * @param urn Unique Resource Name or identifier (for logging)
     * @return Converted data of specified type
     * @throws RuntimeException if conversion fails
     */
    public static <T> T convertData(
            String inputData,
            TypeReference<T> typeReference,
            String className,
            String methodName,
            String urn
    ) {
        try {
            // Validate input
            if (inputData == null || inputData.trim().isEmpty()) {
                log.info(EMPTY_INPUT_DATA);
                return null;
            }
            // Trim and parse JSON
            inputData = inputData.trim();
            // Parse and return
            return objectMapper.readValue(inputData, typeReference);
        } catch (IOException e) {
            log.info(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE);
            throw new RuntimeException(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE,e);
        }
    }


    public static <T> T convertData(
            String inputData,
            TypeReference<T> typeReference
    ) {
        try {
            // Validate input
            if (inputData == null || inputData.trim().isEmpty()) {
                 log.info(EMPTY_INPUT_DATA);
                return null;
            }
            // Trim and parse JSON
            inputData = inputData.trim();
            // Parse and return
            return objectMapper.readValue(inputData, typeReference);
        } catch (IOException e) {
            log.error(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE);
            throw new RuntimeException(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE,e);
        }
    }



    /**
     * Convert Data object response to specified type
     * Useful while converting data from redis or db to any format desired
     * @param inputData Raw object data from Redis
     * @param typeReference TypeReference for the target conversion type
     * @param className Calling class name (for logging)
     * @param methodName Calling method name (for logging)
     * @param urn Unique Resource Name or identifier (for logging)
     * @return Converted data of specified type
     * @throws RuntimeException if conversion fails
     */
    public static <T> T convertData(
            Object inputData,
            TypeReference<T> typeReference,
            String className,
            String methodName,
            String urn
    ) {
        try {
            // Validate input
            if (inputData == null) {
                log.info(INPUT_DATA_IS_EMPTY);
                return null;
            }

            // If input is a string, delegate to existing logic
            if (inputData instanceof String stringInput) {
                return objectMapper.readValue(stringInput, typeReference);
            }

            // If input is a PGObject, handle it separately
            if (isPGObject(inputData)) {
                return handlePGObjectConversion(inputData, typeReference, urn);
            }

            // For all other cases, perform a normal conversion
            String jsonString = objectMapper.writeValueAsString(inputData);
            return objectMapper.readValue(jsonString, typeReference);

        } catch (IOException e) {
            log.info(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE);
            throw new RuntimeException(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE, e);
        } catch (Exception e) {
            log.info(UNEXPECTED_ERROR_DURING_CONVERSION);
            throw new RuntimeException(UNEXPECTED_ERROR_DURING_CONVERSION, e);
        }
    }

    public static <T> T convertData(
            Object inputData,
            TypeReference<T> typeReference
    ) {
        try {
            // Validate input
            if (inputData == null) {
                log.info(INPUT_DATA_IS_EMPTY);
                return null;
            }

            // If input is a string, delegate to existing logic
            if (inputData instanceof String stringInput) {
                return objectMapper.readValue(stringInput, typeReference);
            }

            // If input is a PGObject, handle it separately
            if (isPGObject(inputData)) {
                return handlePGObjectConversion(inputData, typeReference);
            }

            // For all other cases, perform a normal conversion
            String jsonString = objectMapper.writeValueAsString(inputData);
            return objectMapper.readValue(jsonString, typeReference);

        } catch (IOException e) {
             log.info(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE);
            throw new RuntimeException(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE, e);
        } catch (Exception e) {
           log.info(UNEXPECTED_ERROR_DURING_CONVERSION);
            throw new RuntimeException(UNEXPECTED_ERROR_DURING_CONVERSION, e);
        }
    }

    public static JSONObject convertToJSONObject(Object inputData) {
        try {
            if (inputData == null) {
                log.info(INPUT_DATA_IS_EMPTY);
                return null;
            }

            // Convert to Map first using existing logic
            Map<String, Object> map = convertData(inputData, new TypeReference<Map<String, Object>>() {});
            return new JSONObject(map);

        } catch (Exception e) {
            log.error(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE);
            throw new RuntimeException(FAILED_TO_CONVERT_INPUT_DATA_ERROR_MESSAGE, e);
        }
    }


    private static <T> T handlePGObjectConversion(Object inputData, TypeReference<T> typeReference, String urn) {
        try {
            String jsonValue = getPGObjectValue(inputData);

            // Parse the JSON value
            T result = objectMapper.readValue(jsonValue, typeReference);

            // Perform additional JSONB field conversion if required
            if (result instanceof Map) {
                result = (T) convertJsonbFields((Map<String, Object>) result, urn);
            } else if (result instanceof List) {
                result = (T) convertJsonbFieldsInList((List<Object>) result, urn);
            }
            return result;
        } catch (Exception e) {
            log.info(FAILED_TO_CONVERT_PGOBJECT_DATA);
            throw new RuntimeException(FAILED_TO_CONVERT_PGOBJECT_DATA, e);
        }
    }


    private static <T> T handlePGObjectConversion(Object inputData, TypeReference<T> typeReference) {
        try {
            String jsonValue = getPGObjectValue(inputData);

            // Parse the JSON value
            T result = objectMapper.readValue(jsonValue, typeReference);

            // Perform additional JSONB field conversion if required
            if (result instanceof Map) {
                result = (T) convertJsonbFields((Map<String, Object>) result);
            } else if (result instanceof List) {
                result = (T) convertJsonbFieldsInList((List<Object>) result);
            }
            return result;
        } catch (Exception e) {
            log.error(FAILED_TO_CONVERT_PGOBJECT_DATA);
            throw new RuntimeException(FAILED_TO_CONVERT_PGOBJECT_DATA, e);
        }
    }

    private static Map<String, Object> convertJsonbFields(Map<String, Object> input) {
        Map<String, Object> result = new HashMap<>(input);

        for (Map.Entry<String, Object> entry : input.entrySet()) {
            Object value = entry.getValue();
            try {
                if (isPGObject(value) && isPGObjectJsonb(value)) {
                    String jsonValue = getPGObjectValue(value);
                    result.put(entry.getKey(), objectMapper.readValue(jsonValue, Object.class));
                }
            } catch (Exception e) {
                 log.error(FAILED_TO_CONVERT_JSONB_FIELD);
            }
        }
        return result;
    }

    private static Map<String, Object> convertJsonbFields(Map<String, Object> input,String urn) {
        Map<String, Object> result = new HashMap<>(input);

        for (Map.Entry<String, Object> entry : input.entrySet()) {
            Object value = entry.getValue();
            try {
                if (isPGObject(value) && isPGObjectJsonb(value)) {
                    String jsonValue = getPGObjectValue(value);
                    result.put(entry.getKey(), objectMapper.readValue(jsonValue, Object.class));
                }
            } catch (Exception e) {
                log.error(FAILED_TO_CONVERT_JSONB_FIELD);
            }
        }
        return result;
    }

    private static List<Object> convertJsonbFieldsInList(List<Object> input, String urn) {
        List<Object> result = new ArrayList<>(input);

        for (int i = 0; i < input.size(); i++) {
            Object item = input.get(i);
            try {
                if (isPGObject(item) && isPGObjectJsonb(item)) {
                    String jsonValue = getPGObjectValue(item);
                    result.set(i, objectMapper.readValue(jsonValue, Object.class));
                } else if (item instanceof Map<?, ?>) {
                    result.set(i, convertJsonbFields((Map<String, Object>) item));
                }
            } catch (Exception e) {
                log.info(FAILED_TO_CONVERT_JSONB_FIELD_IN_LIST);
            }
        }
        return result;
    }


    private static List<Object> convertJsonbFieldsInList(List<Object> input) {
        List<Object> result = new ArrayList<>(input);

        for (int i = 0; i < input.size(); i++) {
            Object item = input.get(i);
            try {
                if (isPGObject(item) && isPGObjectJsonb(item)) {
                    String jsonValue = getPGObjectValue(item);
                    result.set(i, objectMapper.readValue(jsonValue, Object.class));
                } else if (item instanceof Map<?, ?>) {
                    result.set(i, convertJsonbFields((Map<String, Object>) item));
                }
            } catch (Exception e) {
                 log.info(FAILED_TO_CONVERT_JSONB_FIELD_IN_LIST);
            }
        }
        return result;
    }


    private static boolean isPGObject(Object obj) {
        return obj != null && obj.getClass().getName().equals(ORG_POSTGRESQL_UTIL_PGOBJECT);
    }

    private static boolean isPGObjectJsonb(Object pgObject) throws Exception {
        Class<?> pgObjectClass = Class.forName(ORG_POSTGRESQL_UTIL_PGOBJECT);
        Method getTypeMethod = pgObjectClass.getMethod(GET_TYPE);
        return JSONB.equals(getTypeMethod.invoke(pgObject));
    }

    private static String getPGObjectValue(Object pgObject) throws Exception {
        Class<?> pgObjectClass = Class.forName(ORG_POSTGRESQL_UTIL_PGOBJECT);
        Method getValueMethod = pgObjectClass.getMethod(GET_VALUE);
        return (String) getValueMethod.invoke(pgObject);
    }
}
