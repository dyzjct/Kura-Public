package me.windyteam.kura.utils;

public class Crasher
extends RuntimeException {
    private static final long serialVersionUID = -69696969696L;
    private final String msg;

    public Crasher(String msg) {
        super(msg);
        this.msg = msg;
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public String toString() {
        return this.msg;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

