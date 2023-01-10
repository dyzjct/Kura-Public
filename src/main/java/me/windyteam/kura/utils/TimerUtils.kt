package me.windyteam.kura.utils

class TimerUtils {
    private val current = System.currentTimeMillis()
    var time = currentTime
        protected set
    protected val currentTime: Long
        protected get() = System.currentTimeMillis()

    fun passed(ms: Long): Boolean {
        return System.currentTimeMillis() - time >= ms
    }

    fun passed(ms: Double): Boolean {
        return System.currentTimeMillis() - time >= ms
    }

    fun convertToNS(time: Long): Long {
        return time * 1000000L
    }

    fun setMs(ms: Long) {
        time = System.nanoTime() - convertToNS(ms)
    }

    fun tickAndReset(ms: Long): Boolean {
        if (System.currentTimeMillis() - time >= ms) {
            reset()
            return true
        }
        return false
    }

    fun tickAndReset(ms: Int): Boolean {
        if (System.currentTimeMillis() - time >= ms) {
            reset()
            return true
        }
        return false
    }

    fun tickAndReset(ms: Double): Boolean {
        if (System.currentTimeMillis() - time >= ms) {
            reset()
            return true
        }
        return false
    }

    fun tickAndReset(ms: Float): Boolean {
        if (System.currentTimeMillis() - time >= ms) {
            reset()
            return true
        }
        return false
    }

    fun reset() {
        time = System.currentTimeMillis()
    }

    fun hasReached(var1: Long): Boolean {
        return System.currentTimeMillis() - current >= var1
    }

    fun hasReached(var1: Long, var3: Boolean): Boolean {
        if (var3) {
            reset()
        }
        return System.currentTimeMillis() - current >= var1
    }

    fun passedS(s: Double): Boolean {
        return passedMs(s.toLong() * 1000L)
    }

    fun passedDms(dms: Double): Boolean {
        return passedMs(dms.toLong() * 10L)
    }

    fun passedDs(ds: Double): Boolean {
        return passedMs(ds.toLong() * 100L)
    }

    fun passedMs(ms: Long): Boolean {
        return System.currentTimeMillis() - time >= ms
    }

    fun timePassed(n: Long): Long {
        return System.currentTimeMillis() - n
    }

    val passedTimeMs: Long
        get() = System.currentTimeMillis() - time

    fun passedTicks(ticks: Int): Boolean {
        return this.passed(ticks * 50)
    }

    fun resetTimeSkipTo(p_MS: Long) {
        time = System.currentTimeMillis() + p_MS
    }

    fun passed(ms: Float): Boolean {
        return System.currentTimeMillis() - time >= ms
    }

    fun passed(ms: Int): Boolean {
        return System.currentTimeMillis() - time >= ms
    }

    fun NotPassed(ms: Int): Boolean {
        return System.currentTimeMillis() - time < ms
    }
}