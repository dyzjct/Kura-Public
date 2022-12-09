package me.dyzjct.kura.gui.settingpanel.component.components;

import me.dyzjct.kura.gui.settingpanel.component.ActionEventListener;
import me.dyzjct.kura.gui.settingpanel.component.ValueChangeListener;
import me.dyzjct.kura.module.Module;
import org.lwjgl.input.Keyboard;

public class KeybindButton
extends Button {
    private ValueChangeListener<Integer> listener;
    private boolean listening;
    private Module module;
    private int value;

    public KeybindButton(int preferredWidth, int preferredHeight, Module module) {
        super("", preferredWidth, preferredHeight);
        this.module = module;
        this.initialize();
    }

    public KeybindButton(Module module) {
        super("");
        this.module = module;
        this.initialize();
    }

    private void initialize() {
        this.setOnClickListener(() -> {
            this.listening = !this.listening;
            this.updateState();
        });
        this.updateState();
    }

    @Override
    public void setOnClickListener(ActionEventListener listener) {
        if (this.getOnClickListener() != null) {
            ActionEventListener old = this.getOnClickListener();
            super.setOnClickListener(() -> {
                listener.onActionEvent();
                old.onActionEvent();
            });
        } else {
            super.setOnClickListener(listener);
        }
    }

    @Override
    public boolean keyPressed(int key, char c) {
        if (this.listening) {
            this.listening = false;
            if (Keyboard.getEventKey() != 256 && Keyboard.getEventCharacter() != '\u0000') {
                int newValue;
                int n = newValue = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                if (this.listener != null && this.listener.onValueChange(newValue)) {
                    this.value = newValue;
                }
            }
            this.updateState();
        }
        return super.keyPressed(key, c);
    }

    @Override
    public int getEventPriority() {
        return this.listening ? super.getEventPriority() + 1 : super.getEventPriority();
    }

    private void updateState() {
        if (this.listening) {
            this.setTitle("Press a button...");
        } else {
            try {
                this.setTitle(Keyboard.getKeyName(this.module.getBind()));
            }catch (Exception ignored){}
        }
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
        this.updateState();
    }

    public void setListener(ValueChangeListener<Integer> listener) {
        this.listener = listener;
    }
}

