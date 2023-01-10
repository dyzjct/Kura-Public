package me.windyteam.kura.utils.player;

public class Timer {
    private long time = -1L;

    public boolean passedS(double s) {
        return this.getMs(System.nanoTime() - this.time) >= (long) (s * 1000.0);
    }

    public boolean passedM(double m) {
        return this.getMs(System.nanoTime() - this.time) >= (long) (m * 1000.0 * 60.0);
    }

    public boolean passedDms(double dms) {
        return this.getMs(System.nanoTime() - this.time) >= (long) (dms * 10.0);
    }

    public boolean passedDs(double ds) {
        return this.getMs(System.nanoTime() - this.time) >= (long) (ds * 100.0);
    }

    public boolean passedMs(long ms) {
        return this.getMs(System.nanoTime() - this.time) >= ms;
    }

    public boolean sleep(long time) {
        if ((System.nanoTime() / 1000000L - time) >= time) {
            reset();
            return true;
        }
        return false;
    }
    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }
    public boolean passedTick(double tick) {
        return ((System.currentTimeMillis() - this.time) >= tick * 50);
    }


    public void setMs(long ms) {
        this.time = System.nanoTime() - ms * 1000000L;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }

    public void reset() {
        this.time = System.nanoTime();
    }

    public long getMs(long time) {
        return time / 1000000L;
    }
}

