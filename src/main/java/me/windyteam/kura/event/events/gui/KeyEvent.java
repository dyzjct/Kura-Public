
package me.windyteam.kura.event.events.gui;

import me.windyteam.kura.event.EventStage;

public class KeyEvent
extends EventStage {
    public boolean info;
    public boolean pressed;

    public KeyEvent(int stage, boolean info, boolean pressed) {
        super(stage);
        this.info = info;
        this.pressed = pressed;
    }
}

