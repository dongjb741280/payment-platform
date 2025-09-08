package com.payment.acquiring.service;

public interface CacheService {
    <T> void set(String key, T value, long seconds);
    <T> T get(String key, Class<T> clazz);
    void delete(String key);
}


