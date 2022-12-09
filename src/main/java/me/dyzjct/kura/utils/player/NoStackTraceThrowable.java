package me.dyzjct.kura.utils.player;


public class NoStackTraceThrowable extends RuntimeException {
    public NoStackTraceThrowable() {
        this("STFU NIGGA GET REKT");
    }

    public NoStackTraceThrowable(final String msg) {
        super(msg);
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public String toString() {
        return "FUCK OFF NIGGA UR MUM GAY";
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
