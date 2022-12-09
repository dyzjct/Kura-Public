
package me.dyzjct.kura.event.events.gui;

import me.dyzjct.kura.event.EventStage;

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

