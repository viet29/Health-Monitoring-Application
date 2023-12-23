package com.healthapp.helper;

import java.util.UUID;

public final class ObjectUtils {
    public static String getRandomUUID() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return  randomUUIDString.replace("-", "");
    }
}
