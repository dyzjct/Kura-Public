package me.windyteam.kura.utils.fn;

public class NewTimerUtil {
  private final long current = -1L;
  
  private long time = -1L;
  
  public boolean passedS(double s) {
    return (getMs(System.nanoTime() - this.time) >= (long)(s * 1000.0D));
  }
  
  public boolean passedM(double m) {
    return (getMs(System.nanoTime() - this.time) >= (long)(m * 1000.0D * 60.0D));
  }
  
  public boolean passedDms(double dms) {
    return (getMs(System.nanoTime() - this.time) >= (long)(dms * 10.0D));
  }
  
  public boolean passedDs(double ds) {
    return (getMs(System.nanoTime() - this.time) >= (long)(ds * 100.0D));
  }
  
  public boolean passedMs(long ms) {
    return (getMs(System.nanoTime() - this.time) >= ms);
  }
  
  public boolean passedMss(long ms) {
    return passedNS(convertToNS(ms));
  }
  
  public boolean passedNS(long ns) {
    return (System.nanoTime() - this.time >= ns);
  }
  
  public void setMs(long ms) {
    this.time = System.nanoTime() - ms * 1000000L;
  }
  
  public long getPassedTimeMs() {
    return getMs(System.nanoTime() - this.time);
  }
  
  public NewTimerUtil reset() {
    this.time = System.nanoTime();
    return this;
  }
  
  public long convertToNS(long time) {
    return time * 1000000L;
  }
  
  public long getMs(long time) {
    return time / 1000000L;
  }
  
  public boolean sleep(long time) {
    if (System.nanoTime() / 1000000L - time >= time) {
      reset();
      return true;
    } 
    return false;
  }
  
  public boolean hasReached(long delay) {
    getClass();
    return (System.currentTimeMillis() - -1L >= delay);
  }
  
  public boolean hasReached(long delay, boolean reset) {
    if (reset)
      reset(); 
    getClass();
    return (System.currentTimeMillis() - -1L >= delay);
  }
}
