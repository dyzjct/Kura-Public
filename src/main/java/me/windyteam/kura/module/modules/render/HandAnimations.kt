package me.windyteam.kura.module.modules.render

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraft.util.EnumHand

@Module.Info(name = "HandAnimations", category = Category.RENDER)
object HandAnimations : Module() {
    private val mode = msetting("Anim", Mode.OLD)
    private val swing = msetting("Swing", Swing.Mainhand)
    private val slow = settings("Slow", false)
    override fun onUpdate() {
        if (swing.value === Swing.Offhand) {
            mc.player.swingingHand = EnumHand.OFF_HAND
        }
        if (mode.value === Mode.OLD) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.heldItemMainhand
        }
        if (slow.value) {
            mc.player.addPotionEffect(PotionEffect(MobEffects.MINING_FATIGUE, 255000, 3))
        }
        if (!slow.value) {
            mc.player.removePotionEffect(MobEffects.MINING_FATIGUE)
        }
    }

    override fun onDisable() {
        mc.player.removePotionEffect(MobEffects.MINING_FATIGUE)
    }

    private enum class Mode {
        Normal, OLD
    }

    private enum class Swing {
        Mainhand, Offhand
    }
}