package me.dyzjct.kura.event.events.render;

import me.dyzjct.kura.event.EventStage;

public class TextEvent extends EventStage {

    private String text;

    public TextEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public final TextEvent setText(String text) {
        this.text = text;
        return this;
    }

}