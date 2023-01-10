package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.manager.RotationManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.math.RandomUtil;
import me.windyteam.kura.utils.math.deneb.LagCompensator;
import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.manager.RotationManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.math.RandomUtil;
import me.windyteam.kura.utils.math.deneb.LagCompensator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "32kAuraNew", category = Category.COMBAT)
public class Aura32K extends Module {
    public static Aura32K INSTANCE;

    static {
        INSTANCE = new Aura32K();
    }

    public final Setting<?> server = msetting("Server", Server.Normal);
    public final Setting<Double> hitRange = dsetting("HitRange", 6, 0.1, 8);
    public final Setting<Integer> hitChance = isetting("HitChange", 100, 1, 100);
    public final Setting<Boolean> randomSpeed = bsetting("RandomSpeed", true);
    public final Setting<Integer> randomSpeedValueMin = isetting("RandomSpeedMin", 2, 0, 30).b((BooleanSetting) randomSpeed);
    public final Setting<Integer> randomSpeedValueMax = isetting("RandomSpeedMax", 6, 0, 30).b((BooleanSetting) randomSpeed);
    public final Setting<Integer> delay = isetting("Delay", 2, 0, 20).b((BooleanSetting) randomSpeed);
    public final Setting<Boolean> lethalMode = bsetting("LethalMode", true);
    public final Setting<Integer> lethalCPS = isetting("LethalCPS", 25, 0, 500).b((BooleanSetting) lethalMode);
    public final Setting<Boolean> CriticalCPT = bsetting("CriticalsHit", true);
    public final Setting<Integer> CritCPS = isetting("CritCps", 12, 0, 30).b((BooleanSetting) CriticalCPT);
    public final Setting<Integer> CritCPT = isetting("CritCpt", 1, 0, 30).b((BooleanSetting) CriticalCPT);
    public final Setting<Integer> CritDelay = isetting("CritDelay", 1, 0, 2000).b((BooleanSetting) CriticalCPT);
    public final Setting<Boolean> CPTAttack = bsetting("NormalAttack", true);
    public final Setting<Integer> NCPS = isetting("NormalCps", 15, 0, 100).b((BooleanSetting) CPTAttack);
    public final Setting<Integer> NCPT = isetting("NormalCpt", 0, 0, 30).b((BooleanSetting) CPTAttack);
    public final Setting<Boolean> rotate = bsetting("Rotate", false);
    public final Setting<Boolean> noLag = bsetting("NoLag", true);
    public final Timer cpsTimer = new Timer();
    public final Timer delayTimer = new Timer();
    public int cpsValue;
    public int cpsCritValue;
    public int lethalCPSValue = 0;

    public Aura32K() {
        this.setInstance();
    }

    public static Aura32K getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Aura32K();
        }
        return INSTANCE;
    }

    public void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        RotationManager.resetRotation();
    }

    @SubscribeEvent
    public void onTick(MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        for (Entity target : mc.world.loadedEntityList) {
            if (target == null) {
                lethalCPSValue = 0;
            }
            if (!(target instanceof EntityLivingBase) ||
                    target == mc.player)
                continue;
            if (mc.player.getDistance(target) > hitRange.getValue()
                    || ((EntityLivingBase) target).getHealth() <= 0.0F || (!(target instanceof EntityPlayer)) || (isSuperWeapon(mc.player.getHeldItemMainhand()))) {
                continue;
            }
            if (target == mc.player || FriendManager.isFriend(target.getName()) || mc.player.isDead || mc.player.getHealth() + mc.player.getAbsorptionAmount() <= 0.0f || target.isDead || isSuperWeapon(mc.player.getHeldItemMainhand()))
                continue;
            if (!(mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD))) {
                return;
            }
            if (rotate.getValue()) {
                mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(new Vec3d(target.posX, target.posY, target.posZ))[0];
                mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(new Vec3d(target.posX, target.posY, target.posZ))[0];
                event.setYaw(BlockInteractionHelper.getLegitRotations(new Vec3d(target.posX, target.posY, target.posZ))[0]);
                event.setPitch((BlockInteractionHelper.getLegitRotations(new Vec3d(target.posX, target.posY, target.posZ))[1]));
            }
            if (CPTAttack.getValue()) {
                attack(target);
            }
            if (lethalMode.getValue()) {
                lethalAttack(target);
            }
        }
    }

    public boolean isSuperWeapon(ItemStack item) {
        if (item == null)
            return true;
        if (item.getTagCompound() == null)
            return true;
        if (item.getEnchantmentTagList().getTagType() == 0)
            return true;
        NBTTagList enchants = (NBTTagList) item.getTagCompound().getTag("ench");
        int i = 0;
        if (server.getValue() == Server.Normal) {
            while (i < enchants.tagCount()) {
                NBTTagCompound enchant = enchants.getCompoundTagAt(i);
                if (enchant.getInteger("id") == 16) {
                    int lvl = enchant.getInteger("lvl");
                    if (lvl >= 16)
                        return false;
                    break;
                }
                i++;
            }
        } else if (server.getValue() == Server.Xin) {
            while (i < enchants.tagCount()) {
                NBTTagCompound enchant = enchants.getCompoundTagAt(i);
                if (!(enchant.getInteger("id") == 34 && enchant.getInteger("id") == 20)) {
                    return false;
                }
                i++;
            }
        }
        return true;
    }

    public void attack(Entity entity) {
        int i;
        int delay2 = RandomUtil.nextInt(randomSpeedValueMin.getValue(), randomSpeedValueMax.getValue());
        if (!randomSpeed.getValue()) {
            delay2 = delay.getValue();
        }
        if (!delayTimer.passedTicks(delay2)) {
            return;
        }
        for (i = 0; i < NCPT.getValue(); ++i) {
            if (RandomUtil.nextInt(0, 100) >= hitChance.getValue()) continue;
            if (noLag.getValue()) {
                try {
                    mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                } catch (Exception ignored) {
                }
            } else {
                mc.playerController.attackEntity(mc.player, entity);
            }
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.resetCooldown();
        }
        ++cpsValue;
        if ((float) cpsValue >= LagCompensator.INSTANCE.getTickRate() / (float) (NCPS.getValue())) {
            if (RandomUtil.nextInt(0, 100) < hitChance.getValue()) {
                if (noLag.getValue()) {
                    try {
                        mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                    } catch (Exception ignored) {
                    }
                } else {
                    mc.playerController.attackEntity(mc.player, entity);
                }
            }
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.resetCooldown();
            cpsValue = 0;
        }
        if (CriticalCPT.getValue()) {
            if (cpsTimer.passed((CritDelay.getValue()))) {
                for (i = 0; i < CritCPT.getValue(); ++i) {
                    if (RandomUtil.nextInt(0, 100) >= hitChance.getValue()) continue;
                    if (noLag.getValue()) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                        continue;
                    }
                    mc.playerController.attackEntity(mc.player, entity);
                }
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.resetCooldown();
                cpsTimer.reset();
            }
            ++cpsCritValue;
            if ((float) cpsCritValue >= LagCompensator.INSTANCE.getTickRate() / (float) (CritCPS.getValue())) {
                if (RandomUtil.nextInt(0, 100) < hitChance.getValue()) {
                    if (noLag.getValue()) {
                        try {
                            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                        } catch (Exception ignored) {
                        }
                    } else {
                        mc.playerController.attackEntity(mc.player, entity);
                    }
                }
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.resetCooldown();
                cpsCritValue = 0;
            }
        }
        delayTimer.reset();
    }

    public void lethalAttack(Entity entity) {
        ++lethalCPSValue;
        if ((float) lethalCPSValue >= LagCompensator.INSTANCE.getTickRate() / (float) (lethalCPS.getValue())) {
            if (noLag.getValue()) {
                try {
                    mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                } catch (Exception ignored) {
                }
            } else {
                mc.playerController.attackEntity(mc.player, entity);
            }
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.resetCooldown();
            lethalCPSValue = 0;
        }
    }

    public enum Server {
        Xin,
        Normal
    }

}
