package me.dyzjct.kura.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Module.Info(name = "AntiBurrowHelper", category = Category.PLAYER)
public class AntiBurrowHelper extends Module {
    private final ConcurrentHashMap<EntityPlayer, Integer> players = new ConcurrentHashMap<>();
    List<EntityPlayer> anti_spam = new ArrayList<>();
    List<Entity> burrowedPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        players.clear();
        anti_spam.clear();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        for (Entity entity : mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList())) {
            if (!(entity instanceof EntityPlayer)) {
                continue;
            }
            if (!burrowedPlayers.contains(entity) && isBurrowed(entity)) {
                burrowedPlayers.add(entity);
                ChatUtil.sendMessage(ChatFormatting.RED + entity.getName() + " has just burrowed!");
            } else if (burrowedPlayers.contains(entity) && !isBurrowed(entity)) {
                burrowedPlayers.remove(entity);
                ChatUtil.sendMessage(ChatFormatting.GREEN + entity.getName() + " is no longer burrowed!");
            }
        }
    }

    private boolean isBurrowed(Entity entity) {
        BlockPos entityPos = new BlockPos(roundValueToCenter(entity.posX), entity.posY + .2, roundValueToCenter(entity.posZ));

        if (mc.world.getBlockState(entityPos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(entityPos).getBlock() == Blocks.ENDER_CHEST) {
            return true;
        }

        return false;
    }

    private double roundValueToCenter(double inputVal) {
        double roundVal = Math.round(inputVal);

        if (roundVal > inputVal) {
            roundVal -= 0.5;
        } else if (roundVal <= inputVal) {
            roundVal += 0.5;
        }

        return roundVal;
    }
}