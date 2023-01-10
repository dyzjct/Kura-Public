package me.windyteam.kura.event.events.render.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderItemAnimationEvent extends Event {
    private final ItemStack stack;
    private final EnumHand hand;

    private RenderItemAnimationEvent(ItemStack stack, EnumHand hand) {
        this.stack = stack;
        this.hand = hand;
    }

    public EnumHand getHand() {
        return hand;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static class Transform extends RenderItemAnimationEvent {
        private final float ticks;

        public Transform(ItemStack stack, EnumHand hand, float ticks) {
            super(stack, hand);
            this.ticks = ticks;
        }

        public float getTicks() {
            return ticks;
        }

    }

    public static class Render extends RenderItemAnimationEvent {
        public Render(ItemStack stack, EnumHand hand) {
            super(stack, hand);

        }
    }

}