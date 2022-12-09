/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package me.dyzjct.kura.event.events.player;

import me.dyzjct.kura.event.EventStage;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public final class PlayerApplyCollisionEvent
        extends EventStage {

    private final Entity entity;


    public PlayerApplyCollisionEvent(Entity entity) {
        this.entity = entity;
    }

    public PlayerApplyCollisionEvent(int stage, Entity entity) {
        super(stage);
        this.entity = entity;
    }

    public static PlayerApplyCollisionEvent copy$default(PlayerApplyCollisionEvent playerApplyCollisionEvent, Entity entity, int n, Object object) {
        if ((n & 1) != 0) {
            entity = playerApplyCollisionEvent.entity;
        }
        return playerApplyCollisionEvent.copy(entity);
    }

    public final Entity getEntity() {
        return this.entity;
    }

    public final Entity component1() {
        return this.entity;
    }

    public final PlayerApplyCollisionEvent copy(Entity entity) {
        return new PlayerApplyCollisionEvent(entity);
    }

    public String toString() {
        return "PlayerApplyCollisionEvent(entity=" + this.entity + ")";
    }

    public int hashCode() {
        Entity entity = this.entity;
        return entity != null ? entity.hashCode() : 0;
    }

    public boolean equals(Object object) {
        block3:
        {
            block2:
            {
                if (this == object) break block2;
                if (!(object instanceof PlayerApplyCollisionEvent)) break block3;
                PlayerApplyCollisionEvent playerApplyCollisionEvent = (PlayerApplyCollisionEvent) object;

            }
            return true;
        }
        return false;
    }
}

