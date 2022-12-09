package me.dyzjct.kura.utils.FakeMeteor;

import me.dyzjct.kura.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MeteorSystem {
    public List<Meteor> meteorList = new ArrayList<Meteor>();
    private boolean rainbow;

    public MeteorSystem(int initAmount, boolean rainbow) {
        this.addParticles(initAmount);
        this.rainbow = rainbow;
    }

    public MeteorSystem(int initAmount) {
        this(initAmount, false);
    }

    public void addParticles(int amount) {
        for (int i = 0; i < amount; ++i) {
            this.meteorList.add(Meteor.generateMeteor());
        }
    }

    public void tick() {
        for (Meteor meteor : this.meteorList) {
            meteor.tick();
        }
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public void render() {
        if (Minecraft.getMinecraft().currentScreen == null) {
            return;
        }
        this.meteorList.forEach(meteor -> {
            Color color = this.rainbow ? meteor.randomColor : Color.WHITE;
            RenderUtils.drawLine(meteor.getX(), meteor.getY(), meteor.getX2(), meteor.getY2(), meteor.getLineWidth(), new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)meteor.getAlpha()));
        });
    }
}

