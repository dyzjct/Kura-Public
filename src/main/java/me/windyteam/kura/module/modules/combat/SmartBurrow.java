package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.entity.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;

@Module.Info(name = "Autoburrow", category = Category.COMBAT)
public class SmartBurrow extends Module {
    private static SmartBurrow INSTANCE = new SmartBurrow();
    public Setting<Float> smartRange = fsetting("Smart Range", 2.5F, 1.0F, 10.0F);
    public Setting<Boolean> onlyInHole = bsetting("Hole Only", true);

    public static SmartBurrow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SmartBurrow();
        }

        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (!(Boolean)this.onlyInHole.getValue() || PlayerUtil.isInHole(mc.player)) {
            ArrayList<Entity> entsSorted = (ArrayList)mc.world.loadedEntityList.stream().filter((entity) -> {
                return entity instanceof EntityPlayer && entity != mc.player;
            }).sorted(Comparator.comparing((e) -> {
                return mc.player.getDistance(e);
            })).collect(Collectors.toCollection(ArrayList::new));
            Collections.reverse(entsSorted);
            Burrow burrow = (Burrow) ModuleManager.getModuleByName("Burrow");
            BlockPos pos = new BlockPos(Math.floor(mc.player.getPositionVector().x), Math.floor(mc.player.getPositionVector().y + 0.2D), Math.floor(mc.player.getPositionVector().z));
            Iterator var4 = entsSorted.iterator();

            while(var4.hasNext()) {
                Entity ent = (Entity)var4.next();
                if (ent != mc.player && mc.player.getDistance(ent) < (Float)this.smartRange.getValue() && !PlayerUtil.isInHole(ent) && !burrow.isEnabled() && mc.world.getBlockState(pos).getBlock() instanceof BlockAir) {
                    burrow.enable();
                    if ((Boolean)this.onlyInHole.getValue()) {
                        return;
                    }

                    getInstance().disable();
                }
            }

        }
    }
}
