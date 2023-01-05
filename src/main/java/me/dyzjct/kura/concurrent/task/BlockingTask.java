package me.dyzjct.kura.concurrent.task;

import me.dyzjct.kura.concurrent.thread.BlockingContent;

public interface BlockingTask {
    void invoke(BlockingContent unit);
}
