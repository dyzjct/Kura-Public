package me.windyteam.kura.concurrent.task;

import me.windyteam.kura.concurrent.thread.BlockingContent;
import me.windyteam.kura.concurrent.thread.BlockingContent;

public interface BlockingTask {
    void invoke(BlockingContent unit);
}
