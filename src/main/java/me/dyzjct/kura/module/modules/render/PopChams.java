package me.dyzjct.kura.module.modules.render;

import com.mojang.authlib.GameProfile;
import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.gl.MelonTessellator;
import me.dyzjct.kura.utils.render.TotemPopCham;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Module.Info(name="PopChams", description="Renders when someone pops", category= Category.RENDER)
public class PopChams
        extends Module {
    public static PopChams INSTANCE = new PopChams();
    public BooleanSetting self = this.bsetting("Self", false);
    public IntegerSetting rF = this.isetting("RedFill", 255, 0, 255);
    public IntegerSetting gF = this.isetting("GreenFill", 26, 0, 255);
    public IntegerSetting bF = this.isetting("BlueFill", 42, 0, 255);
    public IntegerSetting aF = this.isetting("AlphaFill", 42, 0, 255);
    public IntegerSetting fadestart = this.isetting("FadeStart", 200, 0, 3000);
    public Setting<Double> fadetime = this.dsetting("FadeTime", 0.5, 0.0, 2.0);
    public BooleanSetting onlyOneEsp = this.bsetting("OnlyOneEsp", true);
    EntityOtherPlayerMP player;
    ModelPlayer playerModel;
    Long startTime;
    double alphaFill;

    public static Color newAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static void glColor(Color color) {
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvents.Receive event) {
        SPacketEntityStatus packet;
        if (event.getPacket() instanceof SPacketEntityStatus && (packet = (SPacketEntityStatus)event.getPacket()).getOpCode() == 35 && packet.getEntity((World)PopChams.mc.world) != null && (((Boolean)this.self.getValue()).booleanValue() || packet.getEntity((World)PopChams.mc.world).getEntityId() != PopChams.mc.player.getEntityId())) {
            GameProfile profile = new GameProfile(PopChams.mc.player.getUniqueID(), "");
            this.player = new EntityOtherPlayerMP((World)PopChams.mc.world, profile);
            this.player.copyLocationAndAnglesFrom(packet.getEntity((World)PopChams.mc.world));
            this.playerModel = new ModelPlayer(0.0f, false);
            this.startTime = System.currentTimeMillis();
            this.playerModel.bipedHead.showModel = false;
            this.playerModel.bipedBody.showModel = false;
            this.playerModel.bipedLeftArmwear.showModel = false;
            this.playerModel.bipedLeftLegwear.showModel = false;
            this.playerModel.bipedRightArmwear.showModel = false;
            this.playerModel.bipedRightLegwear.showModel = false;
            this.alphaFill = ((Integer)this.aF.getValue()).intValue();
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (((Boolean)this.onlyOneEsp.getValue()).booleanValue()) {
            if (this.player == null || PopChams.mc.world == null || PopChams.mc.player == null) {
                return;
            }
            GL11.glLineWidth((float)1.0f);
            Color fillColorS = new Color((Integer)this.rF.getValue(), (Integer)this.bF.getValue(), (Integer)this.gF.getValue(), (Integer)this.aF.getValue());
            int fillA = fillColorS.getAlpha();
            long time = System.currentTimeMillis() - this.startTime - ((Number)this.fadestart.getValue()).longValue();
            if (System.currentTimeMillis() - this.startTime > ((Number)this.fadestart.getValue()).longValue()) {
                double normal = this.normalize(time, 0.0, ((Number)this.fadetime.getValue()).doubleValue());
                normal = MathHelper.clamp((double)normal, (double)0.0, (double)1.0);
                normal = -normal + 1.0;
                fillA *= (int)normal;
            }
            Color fillColor = PopChams.newAlpha(fillColorS, fillA);
            if (this.player != null && this.playerModel != null) {
                MelonTessellator.INSTANCE.prepare(7);
                GL11.glPushAttrib((int)1048575);
                GL11.glEnable((int)2881);
                GL11.glEnable((int)2848);
                if (this.alphaFill > 1.0) {
                    this.alphaFill -= this.fadetime.getValue().doubleValue();
                }
                Color fillFinal = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), (int)this.alphaFill);
                PopChams.glColor(fillFinal);
                GL11.glPolygonMode((int)1032, (int)6914);
                TotemPopCham.renderEntity((EntityLivingBase)this.player, (ModelBase)this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, 1.0f);
                GL11.glPolygonMode((int)1032, (int)6913);
                TotemPopCham.renderEntity((EntityLivingBase)this.player, (ModelBase)this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, 1.0f);
                GL11.glPolygonMode((int)1032, (int)6914);
                GL11.glPopAttrib();
                MelonTessellator.release();
            }
        } else if (!((Boolean)this.onlyOneEsp.getValue()).booleanValue()) {
            new TotemPopCham(this.player, this.playerModel, this.startTime, this.alphaFill);
        }
    }

    public double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}

