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
    @JsonIgnore
    public long start;
    @JsonIgnore
    public long end;
    public long consumeTime;
    public String method;
    public String callingMethod;
    public String callingClassName;
    public String className;
    public int depth;
    public boolean root;
    StackTraceElement callingStackElement;
    StackTraceElement currentStackElement;

    public CallingContext(String method, String classNamePrefix) {
        this.start = System.currentTimeMillis();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        this.method = method;
        boolean haveIgnoreCurrentMethod = false;
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement s = stackTraceElements[i];
            if (s.getClassName().startsWith(classNamePrefix)) {
                if (!haveIgnoreCurrentMethod) {
                    haveIgnoreCurrentMethod = true;
                    this.depth = i;
                    this.currentStackElement = s;
                } else {
                    this.callingStackElement = s;
                    this.callingMethod = s.getMethodName();
                    break;
                }
            }

        }
    }


    public CallingContext() {
        this.start = System.currentTimeMillis();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement s = stackTraceElements[i];

            if ("<init>".equals(s.getMethodName())) {
                if (i + 2 == stackTraceElements.length) {
                    this.method = stackTraceElements[i + 1].getMethodName();
                    this.className = stackTraceElements[i + 1].getClassName();
                    this.callingClassName = stackTraceElements[i + 1].getClassName();
                    this.callingMethod = this.method;
                    this.depth = i + 1;
                    this.callingStackElement = stackTraceElements[i + 1];
                    this.currentStackElement = stackTraceElements[i + 1];
                    break;
                } else {
                    this.currentStackElement = stackTraceElements[i + 1];
                    this.method = stackTraceElements[i + 1].getMethodName();
                    this.className = stackTraceElements[i + 1].getClassName();
                    this.callingMethod = stackTraceElements[i + 2].getMethodName();
                    this.callingClassName = stackTraceElements[i + 2].getClassName();
                    this.callingStackElement = stackTraceElements[i + 2];
                    this.depth = i + 1;
                    break;
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


    public void startContext() {
        CallingContext start = ContextManager.getStartContext();
        if (start.childContexts.isEmpty()) {
            this.parent = start;
            start.childContexts.addLast(this);
            return;
        }

        CallingContext current = start;
        //获取到当前节点插入的位置
        current = findSameDepthCallingContext(current);

        current.childContexts.addLast(this);
    }

    private CallingContext findSameDepthCallingContext(CallingContext current) {
        if (current.root) {
            if (current.childContexts.isEmpty()) {
                return current;
            }

            return findSameDepthCallingContext(current.childContexts.getFirst());

        }


        //遍历当前节点，看是否匹配
        for (CallingContext s : current.parent.childContexts) {
            if (s.currentStackElement.getClassName().equals(this.callingStackElement.getClassName())
                    && s.currentStackElement.getMethodName().equals(this.callingStackElement.getMethodName())
                    && s.currentStackElement.getFileName().equals(this.callingStackElement.getFileName())) {
                System.out.println(this.method + "=>" + this.callingStackElement.getMethodName());
                this.parent = s;
                return s;
            }
        }
        //遍历当前的子节点，看是否匹配
        for (CallingContext s : current.childContexts) {
            CallingContext c = findSameDepthCallingContext(s);
            if (c != null) {
                if (this.parent == null) {
                    this.parent = s;
                }
                return c;

            }
        }

        return null;


    }

    public void endContext() {
        CallingContext callingContext = this;
        callingContext.end = System.currentTimeMillis();
        callingContext.consumeTime = callingContext.end - callingContext.start;

    }


}
