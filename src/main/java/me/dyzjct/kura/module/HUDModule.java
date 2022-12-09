package me.dyzjct.kura.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class HUDModule
extends IModule {
    public HUDModule() {
        this.x = this.getAnnotation().x();
        this.y = this.getAnnotation().y();
        this.width = this.getAnnotation().width();
        this.height = this.getAnnotation().height();
        this.name = this.getAnnotation().name();
        this.category = this.getAnnotation().category();
        this.description = this.getAnnotation().description();
        this.isHUD = true;
        this.onInit();
    }

    private Info getAnnotation() {
        if (this.getClass().isAnnotationPresent(Info.class)) {
            return this.getClass().getAnnotation(Info.class);
        }
        throw new IllegalStateException("No Annotation on class " + this.getClass().getCanonicalName() + "!");
    }

    public void onInit() {
    }

    public void onDragging(int mouseX, int mouseY) {
    }

    public void onMouseRelease() {
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Info {
        public String name();

        public int x() default 0;

        public int y() default 0;

        public int width() default 0;

        public int height() default 0;

        public Category category() default Category.HUD;

        public String description() default "";

        public boolean visible() default true;
    }
}

