package me.dyzjct.kura.utils.particle;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    public List<Particle> particleList = new ArrayList<>();

    public ParticleSystem(int initAmount) {
        this.addParticles(initAmount);
    }

    public static double distance(float x, float y, double x1, double y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }

    public void addParticles(int amount) {
        for (int i = 0; i < amount; ++i) {
            this.particleList.add(Particle.generateParticle());
        }
    }

    public void tick(int delta) {
        for (Particle particle : this.particleList) {
            particle.tick(delta, 0.2f);
        }
    }

    public void render() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);

        if (Minecraft.getMinecraft().currentScreen == null) {
            return;
        }

        particleList.forEach(particle -> {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, (particle.getAlpha() / 255.0f));
            GL11.glPointSize(particle.getSize());
            GL11.glBegin(0);
            GL11.glVertex2f(particle.getX(), particle.getY());
            GL11.glEnd();

            int Width = Mouse.getEventX() * Minecraft.getMinecraft().currentScreen.width / Minecraft.getMinecraft().displayWidth;
            int Height = Minecraft.getMinecraft().currentScreen.height - Mouse.getEventY() * Minecraft.getMinecraft().currentScreen.height / Minecraft.getMinecraft().displayHeight - 1;

            float nearestDistance = 0.0f;
            Particle nearestParticle = null;
            int dist = 100;

            for (Particle particle1 : this.particleList) {
                float distance = particle.getDistanceTo(particle1);
                if (distance > dist || distance(Width, Height, particle.getX(), particle.getY()) > dist && distance(Width, Height, particle1.getX(), particle1.getY()) > dist || nearestDistance > 0.0f && distance > nearestDistance) {
                    continue;
                }
                nearestDistance = distance;
                nearestParticle = particle1;
            }

            if (nearestParticle != null) {
                float alpha = Math.min(1.0f, Math.min(1.0f, 1.0f - nearestDistance / dist));
                drawLine(particle.getX(), particle.getY(), nearestParticle.getX(), nearestParticle.getY(), alpha);
            }
        });

        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    private void drawLine(double f, double f2, double f3, double f4, float f8) {
        GL11.glColor4f((float) 1.0, (float) 1.0, (float) 1.0, f8);
        GL11.glLineWidth(0.5f);
        GL11.glBegin(1);
        GL11.glVertex2f((float) f, (float) f2);
        GL11.glVertex2f((float) f3, (float) f4);
        GL11.glEnd();
    }
}

