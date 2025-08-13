package org.farm.fireflyserver.common.util;
public final class CommandUtil {
    private CommandUtil() {}

    public static void throwIfNullOrBlank(String value, String message) {
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwIfNull(Object value, String message) {
        if(value == null){
            throw new IllegalArgumentException(message);
        }
    }
}

