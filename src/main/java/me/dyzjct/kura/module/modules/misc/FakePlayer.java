package me.dyzjct.kura.module.modules.misc;

import com.mojang.authlib.GameProfile;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.entity.EntityUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.potion.PotionEffect;

import java.util.UUID;

@Module.Info(name = "FakePlayer", category = Category.MISC, description = "Spawns a fake Player")
public class FakePlayer extends Module {
    public Setting<Integer> health = isetting("Health", 12, 0, 36);
    public Setting<String> awa = ssetting("Name", "Ab_noJB");

    @Override
    public String getHudInfo() {
        return "[" + awa.getValue() + "]";
    }

    @Override
    public void onEnable() {
        if (EntityUtil.nullCheck()) {
            return;
        }
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("60569353-f22b-42da-b84b-d706a65c5ddf"), awa.getValue()));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        for (PotionEffect potionEffect : mc.player.getActivePotionEffects()) {
            fakePlayer.addPotionEffect(potionEffect);
        }
        fakePlayer.setHealth(health.getValue());
        fakePlayer.inventory.copyInventory(mc.player.inventory);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        mc.world.addEntityToWorld(-100, fakePlayer);
    }

    @Override
    public void onDisable() {
        mc.world.removeEntityFromWorld(-100);
    }
}
