package me.windyteam.kura.concurrent.repeat;

import me.windyteam.kura.concurrent.task.VoidTask;

public class DelayUnit {

    private final VoidTask task;
    private final long startTime;

    public DelayUnit(VoidTask task, long startTime) {
        this.task = task;
        this.startTime = startTime;
    }

    public boolean invoke() {
        if (System.currentTimeMillis() >= startTime) {
            task.invoke();
            return true;
        }
        return false;
    }

}
