// 
// Decompiled by Procyon v0.5.36
// 

package me.windyteam.kura.utils.entity;

import net.minecraft.potion.PotionType;

public class PotionArrow
{
    public int itemSlot;
    public PotionType potionEffects;
    
    public PotionArrow(final int itemSlot, final PotionType potionEffects) {
        this.itemSlot = itemSlot;
        this.potionEffects = potionEffects;
    }
}
