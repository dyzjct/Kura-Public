package me.windyteam.kura.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class HUDModule extends IModule {
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

    @Retention(value = RetentionPolicy.RUNTIME)
    public @interface Info {
        String name();

        int x() default 0;

        int y() default 0;

        int width() default 0;

        int height() default 0;

        Category category() default Category.HUD;

        String description() default "";

        boolean visible() default true;
    }
}

