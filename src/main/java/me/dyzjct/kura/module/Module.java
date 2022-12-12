package me.dyzjct.kura.module;

import me.dyzjct.kura.setting.ModeSetting;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Module
        extends IModule {
    ModeSetting visible_value;
    public boolean state = false;
    private int offset = 0;

    public Module() {
        this.name = this.getAnnotation().name();
        this.category = this.getAnnotation().category();
        this.description = this.getAnnotation().description();
        this.keyCode = this.getAnnotation().keyCode();
        this.visible_value = new ModeSetting("Visible", this, this.getAnnotation().visible() ? Visible.ON : Visible.OFF);
        this.getSettingList().add(this.visible_value);
        this.isHUD = false;
        this.onInit();
    }

    public static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }

    public static boolean nullCheck() {
        return mc.player == null;
    }

    public boolean isShownOnArray() {
        return visible_value.getValue() == Visible.ON;
    }

    @Override

    public void enable() {
        super.enable();
        /*
        try {
            offset = ShowArrayList.INSTANCE.maxOffset.getValue();
        } catch (Exception ignored) {
        }
         */
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void onInit() {
    }

    private Info getAnnotation() {
        if (this.getClass().isAnnotationPresent(Info.class)) {
            return this.getClass().getAnnotation(Info.class);
        }
        throw new IllegalStateException("No Annotation on class " + this.getClass().getCanonicalName() + "!");
    }

    public enum Visible {
        ON,
        OFF
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    public @interface Info {
        String name();

        String description() default "";

        int keyCode() default 0;

        Category category();

        boolean visible() default true;
    }
}

