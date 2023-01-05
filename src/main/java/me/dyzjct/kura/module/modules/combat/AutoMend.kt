package me.dyzjct.kura.module.modules.combat

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.modules.misc.XCarry
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.Timer
import me.dyzjct.kura.utils.entity.EntityUtil
import me.dyzjct.kura.utils.inventory.InventoryUtil
import me.dyzjct.kura.utils.math.DamageUtil
import me.dyzjct.kura.utils.math.MathUtil
import me.dyzjct.kura.utils.mc.ChatUtil
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

@Module.Info(name = "AutoEXP", category = Category.COMBAT, description = "Automatically mends armour")
class AutoMend : Module() {
    private var delay: Setting<Int> = isetting("Delay", 50, 0, 500)
    private var mendingTakeOff = bsetting("AutoMend", true)
    private var closestEnemy: Setting<Int> = isetting("EnemyRange", 6, 1, 20).b(mendingTakeOff)
    private var mendPercentage: Setting<Int> = isetting("Mend%", 100, 1, 100).b(mendingTakeOff)
    private var updateController: Setting<Boolean> = bsetting("Update", false)
    private var shiftClick: Setting<Boolean> = bsetting("ShiftClick", true)
    private var doneSlots: MutableList<Int> = ArrayList()
    private var timer = Timer()
    private var elytraTimer = Timer()
    private var mending = false
    private var taskList: Queue<InventoryUtil.Task> = ConcurrentLinkedQueue()
    override fun onDisable() {
        if (fullNullCheck()) {
            return
        }
        mc.playerController.syncCurrentPlayItem()
        timer.reset()
        taskList.clear()
        doneSlots.clear()
        elytraTimer.reset()
        mending = false
    }

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        mc.playerController.syncCurrentPlayItem()
        timer.reset()
        elytraTimer.reset()
    }

    override fun onLogout() {
        taskList.clear()
        doneSlots.clear()
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck() || findXpPots() == -1 || event.stage == 1) {
            return
        }
        val oldslot = mc.player.inventory.currentItem
        val XpSlot = InventoryUtil.getItemHotbar(Items.EXPERIENCE_BOTTLE)
        if (XpSlot != -1) {
            event.setRotation(mc.player.rotationYaw, 90f)
            InventoryUtil.switchToHotbarSlot(XpSlot, false)
        } else {
            ChatUtil.sendMessage("Sorry we can't found XP!")
            toggle()
        }
        if (mc.connection != null) {
            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
        }
        InventoryUtil.switchToHotbarSlot(oldslot, false)
        if (taskList.isEmpty()) {
            try {
                if (mendingTakeOff.value && (isSafe || EntityUtil.isSafe(mc.player, 1, false, true))) {
                    mending = true
                    val helm = mc.player.inventoryContainer.getSlot(5).stack
                    if (!helm.isEmpty && DamageUtil.getRoundedDamage(helm) >= mendPercentage.value) {
                        takeOffSlot(5)
                        mending = true
                    }
                    val chest2 = mc.player.inventoryContainer.getSlot(6).stack
                    if (!chest2.isEmpty && DamageUtil.getRoundedDamage(chest2) >= mendPercentage.value) {
                        takeOffSlot(6)
                        mending = true
                    }
                    val legging2 = mc.player.inventoryContainer.getSlot(7).stack
                    if (!legging2.isEmpty && DamageUtil.getRoundedDamage(legging2) >= mendPercentage.value) {
                        takeOffSlot(7)
                        mending = true
                    }
                    val feet2 = mc.player.inventoryContainer.getSlot(8).stack
                    if (!feet2.isEmpty && DamageUtil.getRoundedDamage(feet2) >= mendPercentage.value) {
                        takeOffSlot(8)
                        mending = true
                    }
                }
            } catch (ignored: Exception) {
            }
        }
        if (timer.passedMs(delay.value.toLong())) {
            if (!taskList.isEmpty()) {
                for (i in 0..0) {
                    val task = taskList.poll() ?: continue
                    task.run()
                }
            }
            timer.reset()
        }
        mending = false
    }

    //}
    override fun getHudInfo(): String? {
        return if (mending) {
            TextFormatting.BLUE.toString() + "[" + TextFormatting.RED + "Mending" + TextFormatting.BLUE + "]"
        } else null
    }

    private fun takeOffSlot(slot: Int) {
        if (taskList.isEmpty()) {
            var target = -1
            for (i in InventoryUtil.findEmptySlots(XCarry.getInstance().isEnabled)) {
                if (doneSlots.contains(target)) continue
                target = i
                doneSlots.add(i)
            }
            if (target != -1) {
                if (target < 5 && target > 0 || !shiftClick.value) {
                    taskList.add(InventoryUtil.Task(slot))
                    taskList.add(InventoryUtil.Task(target))
                } else {
                    taskList.add(InventoryUtil.Task(slot, true))
                }
                if (updateController.value) {
                    taskList.add(InventoryUtil.Task())
                }
            }
        }
    }

    private val isSafe: Boolean
        get() {
            val closest = EntityUtil.getClosestEnemy(closestEnemy.value.toDouble()) ?: return true
            return mc.player.getDistanceSq(closest) >= MathUtil.square(closestEnemy.value.toDouble())
        }

    private fun findXpPots(): Int {
        var slot = -1
        for (i in 0..8) {
            if (mc.player.inventory.getStackInSlot(i).getItem() === Items.EXPERIENCE_BOTTLE) {
                slot = i
                break
            }
        }
        return slot
    }

    companion object {
        var INSTANCE = AutoMend()
    }
}