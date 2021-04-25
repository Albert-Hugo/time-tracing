package com.ido.tracing;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;

/**
 * @author Ido
 * @date 2021/4/23 17:49
 */
public class CallingContext {

    @JsonIgnore
    public CallingContext parent;
    public LinkedList<CallingContext> childContexts = new LinkedList<>();
    public long start;
    public long end;
    public long consumeTime;
    public String method;
    public String callingMethod;
    public int depth;

    public CallingContext() {
        this.start = System.currentTimeMillis();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        this.depth = stackTraceElements.length;
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement s = stackTraceElements[i];

            if ("<init>".equals(s.getMethodName())) {
                if (i + 2 == stackTraceElements.length) {
                    this.method = stackTraceElements[i + 1].getMethodName();
                    this.callingMethod = this.method;
                } else {
                    this.method = stackTraceElements[i + 1].getMethodName();
                    this.callingMethod = stackTraceElements[i + 2].getMethodName();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "CallingContext{" +
                ", childContexts=" + childContexts +
                ", consumeTime=" + consumeTime +
                ", method='" + method + '\'' +
                ", callingMethod='" + callingMethod + '\'' +
                ", depth=" + depth +
                '}';
    }


    public static void startContext(CallingContext callingContext) {
        CallingContext start = ContextManager.getStartContext();
        if (start.childContexts.isEmpty()) {
            callingContext.parent = start;
            start.childContexts.addLast(callingContext);
            return;
        }
        int siblingDepth = start.childContexts.getFirst().depth;

        if (siblingDepth == callingContext.depth) {
            callingContext.parent = start;
            start.childContexts.addLast(callingContext);
            return;
        }

        CallingContext current = start;
        while (current.depth != callingContext.depth) {
            if (current.childContexts.isEmpty()) {
                callingContext.parent = current;
                current.childContexts.addLast(callingContext);
                return;
            }
            current = current.childContexts.getLast();
        }
        callingContext.parent = current.parent;
        current.parent.childContexts.addLast(callingContext);
    }

    public static void endContext(CallingContext callingContext) {
        callingContext.end = System.currentTimeMillis();
        callingContext.consumeTime = callingContext.end - callingContext.start;

    }


}
