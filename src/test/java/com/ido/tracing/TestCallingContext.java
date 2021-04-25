package com.ido.tracing;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Ido
 * @date 2021/4/25 10:15
 */
public class TestCallingContext {


    public static void a() {
        CallingContext c = new CallingContext();
        c.startContext();
        System.out.println("a");
        a1();
        b();
        c.endContext();
    }

    public static void a1() {
        CallingContext c = new CallingContext();
        c.startContext();
        System.out.println("a1");
        c.endContext();
    }

    public static void b() {
        CallingContext c = new CallingContext();
        c.startContext();
        System.out.println("b");
        c();
        c.endContext();
    }

    public static void c() {
        CallingContext c = new CallingContext();
        c.startContext();
        System.out.println("c");
        d();
        c.endContext();
    }

    public static void d() {
        CallingContext c = new CallingContext();
        c.startContext();
        System.out.println("d");
        c.endContext();
    }

    static {
        ContextManager.init();
    }

    public static void main(String[] args) throws JsonProcessingException {
        a();
        ContextManager.print();
        ContextManager.cleanContext();

    }
}
