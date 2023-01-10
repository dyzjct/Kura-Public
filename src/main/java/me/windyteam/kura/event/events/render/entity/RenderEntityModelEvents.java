package me.windyteam.kura.event.events.render.entity;

import me.windyteam.kura.event.EventStage;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class RenderEntityModelEvents extends EventStage {
    private final ModelBase modelBase;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float age;
    private final float headYaw;
    private final float headPitch;
    private final float scale;
    private Entity entity;

    public RenderEntityModelEvents(int stage, final ModelBase modelBase, final Entity entity, final float limbSwing, final float limbSwingAmount, final float age, final float headYaw, final float headPitch, final float scale) {
        super(stage);
        this.modelBase = modelBase;
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.age = age;
        this.headYaw = headYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }

    public ModelBase getModelBase() {
        return this.modelBase;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(final Entity entity) {
        this.entity = entity;
    }

    public float getLimbSwing() {
        return this.limbSwing;
    }

    public float getLimbSwingAmount() {
        return this.limbSwingAmount;
    }

    public float getAge() {
        return this.age;
    }

    public float getHeadYaw() {
        return this.headYaw;
    }

    public float getHeadPitch() {
        return this.headPitch;
    }

    public float getScale() {
        return this.scale;
    }
}
