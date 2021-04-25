package com.ido.tracing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ido
 * @date 2021/4/25 10:41
 */
public class ContextManager {
    private static ThreadLocal<CallingContext> threadLocal = new ThreadLocal<>();
    private static ObjectMapper o = new ObjectMapper();

    public static void print() throws JsonProcessingException {
        System.out.println(o.writeValueAsString(threadLocal.get()));
    }

    public static void init() {
        CallingContext start = new CallingContext();
        threadLocal.set(start);
    }

    public static void cleanContext() {
        threadLocal.remove();
    }

    public static CallingContext getStartContext() {
        CallingContext start = threadLocal.get();
        return start;
    }

}
