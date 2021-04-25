package com.ido.tracing;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Ido
 * @date 2021/4/25 10:15
 */
public class TestCallingContext {


    public static void a() {
        CallingContext c = new CallingContext();
        CallingContext.startContext(c);
        System.out.println("a");
        a1();
        b();
        CallingContext.endContext(c);
    }

    public static void a1() {
        CallingContext c = new CallingContext();
        CallingContext.startContext(c);
        System.out.println("a1");
        CallingContext.endContext(c);
    }

    public static void b() {
        CallingContext c = new CallingContext();
        CallingContext.startContext(c);
        System.out.println("b");
        c();
        CallingContext.endContext(c);
    }

    public static void c() {
        CallingContext c = new CallingContext();
        CallingContext.startContext(c);
        System.out.println("c");
        d();
        CallingContext.endContext(c);
    }

    public static void d() {
        CallingContext c = new CallingContext();
        CallingContext.startContext(c);
        System.out.println("d");
        CallingContext.endContext(c);
    }

    static {
        CallingContext.init();
    }

    public static void main(String[] args) throws JsonProcessingException {
        a();
        CallingContext.print();

    }
}
