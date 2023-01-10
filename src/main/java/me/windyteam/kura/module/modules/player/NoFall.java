package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.movement.ElytraPlus;
import me.windyteam.kura.setting.ModeSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.entity.EntityUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by 086 on 19/11/2017.
 * Updated by S-B99 on 05/03/20
 */
@Module.Info(category = Category.PLAYER, description = "Prevents fall damage", name = "NoFall")
public class NoFall extends Module {

    public ModeSetting<FallMode> fallMode = msetting("Mode", FallMode.PACKET);
    public Setting<Boolean> pickup = bsetting("PickUp", true).m(fallMode, FallMode.BUCKET);
    public Setting<Integer> distance = isetting("Distance", 3, 1, 10).m(fallMode, FallMode.BUCKET);
    public Setting<Integer> pickupDelay = isetting("Delay", 300, 100, 1000).m(fallMode, FallMode.BUCKET);
    public long last = 0;

    @SubscribeEvent
    public void send(PacketEvents.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (ModuleManager.getModuleByClass(ElytraPlus.class).isEnabled()){
            return;
        }
        if ((fallMode.getValue().equals(FallMode.PACKET)) && event.getPacket() instanceof CPacketPlayer) {
            ((CPacketPlayer) event.getPacket()).onGround = true;
        }
    }

    @Override
    public void onUpdate() {
        if (ModuleManager.getModuleByClass(ElytraPlus.class).isEnabled()){
            return;
        }
        if ((fallMode.getValue().equals(FallMode.BUCKET)) && mc.player.fallDistance >= distance.getValue() && !EntityUtil.isAboveWater(mc.player) && System.currentTimeMillis() - last > 100) {
            Vec3d posVec = mc.player.getPositionVector();
            RayTraceResult result = mc.world.rayTraceBlocks(posVec, posVec.add(0, -5.33f, 0), true, true, false);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                EnumHand hand = EnumHand.MAIN_HAND;
                if (mc.player.getHeldItemOffhand().getItem() == Items.WATER_BUCKET) hand = EnumHand.OFF_HAND;
                else if (mc.player.getHeldItemMainhand().getItem() != Items.WATER_BUCKET) {
                    for (int i = 0; i < 9; i++)
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.WATER_BUCKET) {
                            mc.player.inventory.currentItem = i;
                            mc.player.rotationPitch = 90;
                            last = System.currentTimeMillis();
                            return;
                        }
                    return;
                }
                mc.player.rotationPitch = 90;
                mc.playerController.processRightClick(mc.player, mc.world, hand);
            }
            if (pickup.getValue()) {
                new Thread(() -> {
                    try {
                        Thread.sleep(pickupDelay.getValue());
                    } catch (InterruptedException ignored) {
                    }
                    mc.player.rotationPitch = 90;
                    mc.rightClickMouse();
                }).start();
            }
        }
    }

    public enum FallMode {
        BUCKET, PACKET
    }
}
