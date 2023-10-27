package com.sazer.server.service;

import java.util.HashMap;
import java.util.Map;

public class SocketThreadCollection {
    public static Map<String, SocketThread> map = new HashMap<>();

    public static void add(String userId, SocketThread thread) {
        map.put(userId, thread);
    }

    public static SocketThread get(String userId) {
        return map.getOrDefault(userId, null);
    }
}
