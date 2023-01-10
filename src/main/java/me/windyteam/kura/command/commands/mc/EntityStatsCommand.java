package me.windyteam.kura.command.commands.mc;

import me.windyteam.kura.command.Command;
import java.math.BigDecimal;
import java.math.RoundingMode;

import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractHorse;

public class EntityStatsCommand
extends Command {
    public EntityStatsCommand() {
        super("entitystats", null, "estats");
        this.setDescription("Print the statistics of the entity you're currently riding");
    }

    @Override
    public void call(String[] args2) {
        if (this.mc.player.getRidingEntity() != null && this.mc.player.getRidingEntity() instanceof AbstractHorse) {
            AbstractHorse horse = (AbstractHorse)this.mc.player.getRidingEntity();
            float maxHealth = horse.getMaxHealth();
            double speed = EntityStatsCommand.round(43.17 * (double)horse.getAIMoveSpeed(), 2);
            double jump = EntityStatsCommand.round(-0.1817584952 * Math.pow(horse.getHorseJumpStrength(), 3.0) + 3.689713992 * Math.pow(horse.getHorseJumpStrength(), 2.0) + 2.128599134 * horse.getHorseJumpStrength() - 0.343930367, 4);
            String ownerId = horse.getOwnerUniqueId() == null ? "Not tamed." : horse.getOwnerUniqueId().toString();
            StringBuilder builder = new StringBuilder("§6Entity Statistics:");
            builder.append("\n§cMax Health: ").append(maxHealth);
            builder.append("\n§cSpeed: ").append(speed);
            builder.append("\n§cJump: ").append(jump);
            builder.append("\n§cOwner: ").append(ownerId);
            ChatUtil.sendMessage(builder.toString());
        } else if (this.mc.player.getRidingEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase)this.mc.player.getRidingEntity();
            ChatUtil.sendMessage("§6Entity Stats:\n§cMax Health: §b" + entity.getMaxHealth() + " §2HP\n§cSpeed: §b" + EntityStatsCommand.round(43.17 * (double)entity.getAIMoveSpeed(), 2) + " §2m/s");
        } else {
            ChatUtil.NoSpam.sendErrorMessage("§4§lError: §cNot riding a compatible entity.");
        }
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

