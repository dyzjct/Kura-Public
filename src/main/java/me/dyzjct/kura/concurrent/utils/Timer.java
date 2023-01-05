package me.dyzjct.kura.concurrent.utils;

/**
 * Created by B_312 on 05/01/2021
 */
public final class Timer {

    private long time;
    public boolean passedMs(long ms) {
        return System.currentTimeMillis() - this.time >= ms;
    }
    public Timer() {
        time = -1;
    }
    public long getPassedTimeMs() {
        return System.currentTimeMillis() - this.time;
    }

    public boolean passedS(double s) {
        return this.passedMs((long) s * 1000L);
    }
    public void resetTimeSkipTo(final long p_MS) {
        this.time = System.currentTimeMillis() + p_MS;
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - time;
    }
    public boolean passed(int ms) {
        return ((System.currentTimeMillis() - this.time) >= ms);
    }

    public void reset() {
        time = System.currentTimeMillis();
    }

}