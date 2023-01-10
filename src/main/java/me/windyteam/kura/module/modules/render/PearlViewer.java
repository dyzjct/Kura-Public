// 
// Decompiled by Procyon v0.5.36
// 

package me.windyteam.kura.module.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.gl.XG42Tessellator;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.*;

@Module.Info(name = "PearlViewer", category = Category.RENDER)
public class PearlViewer extends Module {
    private final HashMap<UUID, List<Vec3d>> poses = new HashMap<>();
    private final HashMap<UUID, Double> time = new HashMap<>();
    private final Setting<Boolean> chat = bsetting("Chat", true);
    private final Setting<Boolean> render = bsetting("Render", true);
    private final Setting<Double> renderTime = dsetting("RenderTime", 5, 0, 30);
    private final Setting<Integer> Thick = isetting("Thick", 3, 0, 10);

    @Override
    public void onUpdate() {
        Iterator<?> iter = (new HashMap<>(this.time)).entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<UUID, Double> e = (Map.Entry<UUID, Double>) iter.next();

            if (e.getValue() <= 0.0D) {
                this.poses.remove(e.getKey());
                this.time.remove(e.getKey());
            } else {
                this.time.replace(e.getKey(), e.getValue() - 0.05D);
            }
        }

        iter = mc.world.loadedEntityList.iterator();

        while (true) {
            Entity e;
            do {
                if (!iter.hasNext()) {
                    return;
                }

                e = (Entity) iter.next();
            } while (!(e instanceof EntityEnderPearl));

            if (!this.poses.containsKey(e.getUniqueID())) {
                if (chat.getValue()) {
                    for (net.minecraft.entity.player.EntityPlayer entityPlayer : mc.world.playerEntities) {
                        if (entityPlayer.getDistance(e) < 4.0F && !((Entity) entityPlayer).getName().equals(mc.player.getName())) {
                            ChatUtil.sendMessage(ChatFormatting.RED + entityPlayer.getName() + ChatFormatting.AQUA + " Threw a Pearl !");
                            break;
                        }
                    }
                }

                this.poses.put(e.getUniqueID(), new ArrayList<>(Collections.singletonList(e.getPositionVector())));
                this.time.put(e.getUniqueID(), this.renderTime.getValue());
            } else {
                this.time.replace(e.getUniqueID(), this.renderTime.getValue());
                List<Vec3d> v = this.poses.get(e.getUniqueID());
                v.add(e.getPositionVector());
            }
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (fullNullCheck()) {
            return;
        }
        XG42Tessellator.prepare_gl();
        if (this.render.getValue()) {
            GL11.glLineWidth((float) Thick.getValue());
            Iterator<Map.Entry<UUID, List<Vec3d>>> posIter = this.poses.entrySet().iterator();

            Map.Entry<?, ?> e;
            do {
                if (!posIter.hasNext()) {
                    XG42Tessellator.glCleanup();
                    return;
                }
                e = posIter.next();
            } while (((List) e.getValue()).size() <= 2);
            GL11.glBegin(1);
            Random rand = new Random(e.getKey().hashCode());
            double r = 0.5D + rand.nextDouble() / 2.0D;
            double g = 0.5D + rand.nextDouble() / 2.0D;
            double b = 0.5D + rand.nextDouble() / 2.0D;
            GL11.glColor3d(r, g, b);
            double[] rPos = XG42Tessellator.rPos();
            for (int i = 1; i < ((List) e.getValue()).size(); ++i) {
                GL11.glVertex3d(((Vec3d) ((List) e.getValue()).get(i)).x - rPos[0], ((Vec3d) ((List) e.getValue()).get(i)).y - rPos[1], ((Vec3d) ((List) e.getValue()).get(i)).z - rPos[2]);
                GL11.glVertex3d(((Vec3d) ((List) e.getValue()).get(i - 1)).x - rPos[0], ((Vec3d) ((List) e.getValue()).get(i - 1)).y - rPos[1], ((Vec3d) ((List) e.getValue()).get(i - 1)).z - rPos[2]);
            }
            GL11.glEnd();
        }
    }

}
