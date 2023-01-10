package me.windyteam.kura.event.events.render;

import me.windyteam.kura.event.EventStage;

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