package me.windyteam.kura.module.modules.xddd

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.modules.combat.AutoTotem
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.entity.CrystalUtil
import me.windyteam.kura.utils.entity.HoleUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.*
import org.lwjgl.input.Mouse

@Module.Info(name = "SmartOffHand", category = Category.XDDD, description = "StupidOffHand")
class SmartOffHand : Module() {
    private var totems = 0
    private var count = 0
    private var mode = msetting("Mode", Mode.Crystal)
    private var delay = isetting("Delay", 0, 0, 1000)
    private var totem = bsetting("SwitchTotem", true)
    private var sbHealth = dsetting("Health", 11.0, 0.0, 36.0)
    private var autoSwitch = bsetting("SwitchGap", true)
    private var switchMode = msetting("GapWhen", SMode.RClick).b(autoSwitch)
    private var elytra = bsetting("CheckElytra", true)
    private var holeCheck = bsetting("CheckHole", false)
    private var holeSwitch = dsetting("HoleHealth", 8.0, 0.0, 36.0).b(holeCheck)
    private var crystalCalculate = bsetting("CalculateDmg", true)
    private var maxSelfDmg = dsetting("MaxSelfDmg", 26.0, 0.0, 36.0).b(crystalCalculate)
    override fun onUpdate() {
        val shouldSwitch: Boolean
        val sOffhandItem: Item
        if (fullNullCheck()) {
            return
        }
        if (!AutoTotem.INSTANCE.soft.value) {
            AutoTotem.INSTANCE.soft.value = true
        }
        var crystals = mc.player.inventory.mainInventory.stream()
            .filter { itemStack: ItemStack -> itemStack.getItem() === Items.END_CRYSTAL }
            .mapToInt { obj: ItemStack -> obj.count }.sum()
        if (mc.player.heldItemOffhand.getItem() === Items.END_CRYSTAL) {
            crystals += mc.player.heldItemOffhand.count
        }
        var gapple = mc.player.inventory.mainInventory.stream()
            .filter { itemStack: ItemStack -> itemStack.getItem() === Items.GOLDEN_APPLE }
            .mapToInt { obj: ItemStack -> obj.count }.sum()
        if (mc.player.heldItemOffhand.getItem() === Items.GOLDEN_APPLE) {
            gapple += mc.player.heldItemOffhand.count
        }
        totems = mc.player.inventory.mainInventory.stream()
            .filter { itemStack: ItemStack -> itemStack.getItem() === Items.TOTEM_OF_UNDYING }
            .mapToInt { obj: ItemStack -> obj.count }.sum()
        if (mc.player.heldItemOffhand.getItem() === Items.TOTEM_OF_UNDYING) {
            ++totems
        }
        var item: Item? = null
        if (!mc.player.heldItemOffhand.isEmpty()) {
            item = mc.player.heldItemOffhand.getItem()
        }
        count =
            if (item != null) (if (item == Items.END_CRYSTAL) crystals else if (item == Items.TOTEM_OF_UNDYING) totems else gapple) else 0
        val handItem = mc.player.heldItemMainhand.getItem()
        val offhandItem = if (mode.value as Mode? == Mode.Crystal as Any) Items.END_CRYSTAL else Items.GOLDEN_APPLE
        sOffhandItem = if (mode.value as Mode? == Mode.Crystal as Any) Items.GOLDEN_APPLE else Items.END_CRYSTAL
        val item2 = sOffhandItem
        if (switchMode.value as SMode? == SMode.Sword as Any) {
            shouldSwitch = mc.player.heldItemMainhand.getItem() is ItemSword && Mouse.isButtonDown(
                1
            ) && autoSwitch.value as Boolean != false
        } else {
            shouldSwitch =
                Mouse.isButtonDown(1) && autoSwitch.value as Boolean != false && handItem !is ItemFood && handItem !is ItemExpBottle && handItem !is ItemBlock
            val bl = shouldSwitch
        }
        if (shouldTotem() && getItemSlot(Items.TOTEM_OF_UNDYING) != -1) {
            switch_Totem()
        } else if (shouldSwitch && getItemSlot(sOffhandItem) != -1) {
            if (mc.player.heldItemOffhand.getItem() != sOffhandItem) {
                val slot =
                    if (getItemSlot(sOffhandItem) < 9) getItemSlot(sOffhandItem) + 36 else getItemSlot(sOffhandItem)
                switchTo(slot)
            }
        } else if (getItemSlot(offhandItem) != -1) {
            val slot: Int
            slot = if (getItemSlot(offhandItem) < 9) getItemSlot(offhandItem) + 36 else getItemSlot(offhandItem)
            val n = slot
            if (mc.player.heldItemOffhand.getItem() != offhandItem) {
                switchTo(slot)
            }
        } else {
            switch_Totem()
        }
    }

    private fun shouldTotem(): Boolean {
        return if (totem.value as Boolean) {
            checkHealth() || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)
                .getItem() === Items.ELYTRA && elytra.value as Boolean || mc.player.fallDistance >= 5.0f || HoleUtil.isPlayerInHole() && holeCheck.value as Boolean && (mc.player.health + mc.player.getAbsorptionAmount()).toDouble() <= holeSwitch.value as Double || crystalCalculate.value as Boolean && calcHealth()
        } else false
    }

    fun calcHealth(): Boolean {
        var maxDmg = 0.5
        for (entity in ArrayList(mc.world.loadedEntityList)) {
            if (entity !is EntityEnderCrystal) {
                continue
            }
            if (mc.player.getDistance(entity) > 12.0f) {
                continue
            }
            val d = CrystalUtil.calculateDamage(entity.posX, entity.posY, entity.posZ, mc.player as Entity).toDouble()
            if (d <= maxDmg) {
                continue
            }
            maxDmg = d
        }
        return maxDmg - 0.5 > mc.player.health + mc.player.getAbsorptionAmount() || maxDmg > maxSelfDmg.value
    }

    fun checkHealth(): Boolean {
        val lowHealth = (mc.player.health + mc.player.getAbsorptionAmount()).toDouble() <= sbHealth.value as Double
        val notInHoleAndLowHealth = lowHealth && !HoleUtil.isPlayerInHole()
        return if (holeCheck.value as Boolean != false) notInHoleAndLowHealth else lowHealth
    }

    fun switch_Totem() {
        if (totems != 0 && mc.player.heldItemOffhand.getItem() != Items.TOTEM_OF_UNDYING) {
            val slot =
                if (getItemSlot(Items.TOTEM_OF_UNDYING) < 9) getItemSlot(Items.TOTEM_OF_UNDYING) + 36 else getItemSlot(
                    Items.TOTEM_OF_UNDYING
                )
            switchTo(slot)
        }
    }

    fun switchTo(slot: Int) {
        try {
            if (timerUtils.passed(delay.value)) {
                mc.playerController.windowClick(
                    mc.player.inventoryContainer.windowId,
                    slot,
                    0,
                    ClickType.PICKUP,
                    mc.player as EntityPlayer
                )
                mc.playerController.windowClick(
                    mc.player.inventoryContainer.windowId,
                    45,
                    0,
                    ClickType.PICKUP,
                    mc.player as EntityPlayer
                )
                mc.playerController.windowClick(
                    mc.player.inventoryContainer.windowId,
                    slot,
                    0,
                    ClickType.PICKUP,
                    mc.player as EntityPlayer
                )
                timerUtils.reset()
            }
        } catch (exception: Exception) {
            // empty catch block
        }
    }

    fun getItemSlot(input: Item): Int {
        var itemSlot = -1
        for (i in 45 downTo 1) {
            if (mc.player.inventory.getStackInSlot(i).getItem() !== input) continue
            itemSlot = i
            break
        }
        return itemSlot
    }

    override fun getHudInfo(): String {
        if (mc.player.heldItemOffhand.getItem() === Items.TOTEM_OF_UNDYING) {
            return "Totem"
        }
        if (mc.player.heldItemOffhand.getItem() === Items.END_CRYSTAL) {
            return "Crystal"
        }
        if (mc.player.heldItemOffhand.getItem() === Items.GOLDEN_APPLE) {
            return "Gapple"
        }
        return if (mc.player.heldItemOffhand.getItem() === Items.BED) {
            "Bed"
        } else "None"
    }

    enum class SMode {
        RClick, Sword
    }

    enum class Mode {
        Crystal, Gap
    }

    companion object {
        var timerUtils = TimerUtils()
    }
}