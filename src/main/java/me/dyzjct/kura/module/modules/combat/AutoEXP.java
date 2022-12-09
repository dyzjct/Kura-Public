package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.misc.XCarry;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.math.DamageUtil;
import me.dyzjct.kura.utils.math.MathUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Info(name = "AutoEXP", category = Category.COMBAT, description = "Automatically mends armour")
public class AutoEXP extends Module {
    public static AutoEXP INSTANCE = new AutoEXP();
    public Setting<Integer> delay = isetting("Delay", 50, 0, 500);
    public BooleanSetting mendingTakeOff = bsetting("AutoMend", true);
    public Setting<Integer> closestEnemy = isetting("EnemyRange", 6, 1, 20).b(mendingTakeOff);
    public Setting<Integer> mend_percentage = isetting("Mend%", 100, 1, 100).b(mendingTakeOff);
    public Setting<Boolean> updateController = bsetting("Update", false);
    public Setting<Boolean> shiftClick = bsetting("ShiftClick", true);
    public List<Integer> doneSlots = new ArrayList<>();
    public Timer timer = new Timer();
    public Timer elytraTimer = new Timer();
    public boolean mending = false;
    public Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        mc.playerController.syncCurrentPlayItem();
        timer.reset();
        taskList.clear();
        doneSlots.clear();
        elytraTimer.reset();
        mending = false;
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        mc.playerController.syncCurrentPlayItem();
        timer.reset();
        elytraTimer.reset();
    }

    @Override
    public void onLogout() {
        taskList.clear();
        doneSlots.clear();
    }

    @SubscribeEvent
    public void onTick(MotionUpdateEvent.Tick event) {
        if (fullNullCheck() || findXpPots() == -1 || event.getStage() == 1) {
            return;
        }
        int oldslot = mc.player.inventory.currentItem;
        int XpSlot = InventoryUtil.getItemHotbar(Items.EXPERIENCE_BOTTLE);
        if (XpSlot != -1) {
            event.setRotation(mc.player.rotationYaw, 90f);
            InventoryUtil.switchToHotbarSlot(XpSlot, false);
        } else {
            ChatUtil.sendMessage("Sorry we can't found XP!");
            toggle();
        }
        if (mc.getConnection() != null) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        }
        InventoryUtil.switchToHotbarSlot(oldslot, false);
        if (taskList.isEmpty()) {
            try {
                if (mendingTakeOff.getValue() && (isSafe() || EntityUtil.isSafe(mc.player, 1, false, true))) {
                    mending = true;
                    ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
                    if (!helm.isEmpty && (DamageUtil.getRoundedDamage(helm)) >= mend_percentage.getValue()) {
                        takeOffSlot(5);
                        mending = true;
                    }
                    ItemStack chest2 = mc.player.inventoryContainer.getSlot(6).getStack();
                    if (!chest2.isEmpty && (DamageUtil.getRoundedDamage(chest2)) >= mend_percentage.getValue()) {
                        takeOffSlot(6);
                        mending = true;
                    }
                    ItemStack legging2 = mc.player.inventoryContainer.getSlot(7).getStack();
                    if (!legging2.isEmpty && DamageUtil.getRoundedDamage(legging2) >= mend_percentage.getValue()) {
                        takeOffSlot(7);
                        mending = true;
                    }
                    ItemStack feet2 = mc.player.inventoryContainer.getSlot(8).getStack();
                    if (!feet2.isEmpty && DamageUtil.getRoundedDamage(feet2) >= mend_percentage.getValue()) {
                        takeOffSlot(8);
                        mending = true;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (timer.passedMs(delay.getValue())) {
            if (!taskList.isEmpty()) {
                for (int i = 0; i < 1; ++i) {
                    InventoryUtil.Task task = taskList.poll();
                    if (task == null) continue;
                    task.run();
                }
            }
            timer.reset();
        }
        mending = false;
    }
    //}

    @Override
    public String getHudInfo() {
        if (mending) {
            return TextFormatting.BLUE + "[" + TextFormatting.RED + "Mending" + TextFormatting.BLUE + "]";
        }
        return null;
    }

    private void takeOffSlot(int slot) {
        if (taskList.isEmpty()) {
            int target = -1;
            for (int i : InventoryUtil.findEmptySlots(XCarry.getInstance().isEnabled())) {
                if (doneSlots.contains(target)) continue;
                target = i;
                doneSlots.add(i);
            }
            if (target != -1) {
                if (target < 5 && target > 0 || !shiftClick.getValue()) {
                    taskList.add(new InventoryUtil.Task(slot));
                    taskList.add(new InventoryUtil.Task(target));
                } else {
                    taskList.add(new InventoryUtil.Task(slot, true));
                }
                if (updateController.getValue()) {
                    taskList.add(new InventoryUtil.Task());
                }
            }
        }
    }

    private boolean isSafe() {
        EntityPlayer closest = EntityUtil.getClosestEnemy(closestEnemy.getValue());
        if (closest == null) {
            return true;
        }
        return mc.player.getDistanceSq(closest) >= MathUtil.square(closestEnemy.getValue());
    }

    private int findXpPots() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}
