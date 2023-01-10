package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

@Module.Info(name = "MCF", description = "MCF", category = Category.MISC)
public class MCF extends Module {

    private boolean clicked;

    @Override
    public void onUpdate() {
        if (mc.currentScreen == null) {
            if (Mouse.isButtonDown(2)) {
                if (!this.clicked) {
                    final RayTraceResult result = mc.objectMouseOver;
                    if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
                        final Entity entity = result.entityHit;
                        if (entity instanceof EntityPlayer) {
                            String name = entity.getName();
                            if (FriendManager.isFriend(name)) {
                                FriendManager.removeFriend(name);
                                ChatUtil.sendMessage(ChatUtil.SECTIONSIGN + "b" + name + ChatUtil.SECTIONSIGN + "r" + " has been unfriended.");
                            } else {
                                FriendManager.addFriend(name);
                                ChatUtil.sendMessage(ChatUtil.SECTIONSIGN + "b" + name + ChatUtil.SECTIONSIGN + "r" + " has been friended.");
                            }
                        }
                    }
                }
                this.clicked = true;
            } else {
                this.clicked = false;
            }
        }
    }
}
