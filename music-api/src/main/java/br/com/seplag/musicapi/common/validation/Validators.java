package br.com.seplag.musicapi.common.validation;

import java.util.Collection;

public class Validators {

    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static void requireNonBlank(String str, String fieldName) {
        if (isBlank(str)) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
    }

    public static void requireNonNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
}
