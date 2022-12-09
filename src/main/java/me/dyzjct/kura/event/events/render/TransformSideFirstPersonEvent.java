package me.dyzjct.kura.event.events.render;

import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TransformSideFirstPersonEvent extends Event {
	private final EnumHandSide enumHandSide;
	public TransformSideFirstPersonEvent(EnumHandSide enumHandSide){
		this.enumHandSide = enumHandSide;
	}
	public EnumHandSide getEnumHandSide(){
		return this.enumHandSide;
	}
}