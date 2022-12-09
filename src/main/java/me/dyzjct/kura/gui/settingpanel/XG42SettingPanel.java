package me.dyzjct.kura.gui.settingpanel;

import me.dyzjct.kura.Kura;
import me.dyzjct.kura.gui.settingpanel.component.AbstractComponent;
import me.dyzjct.kura.gui.settingpanel.component.ActionEventListener;
import me.dyzjct.kura.gui.settingpanel.component.components.Label;
import me.dyzjct.kura.gui.settingpanel.component.components.TextField;
import me.dyzjct.kura.gui.settingpanel.layout.GridLayout;
import me.dyzjct.kura.gui.settingpanel.utils.UserValueChangeListener;
import me.dyzjct.kura.gui.settingpanel.utils.Utils;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.IModule;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.utils.Wrapper;
import me.dyzjct.kura.utils.particle.ParticleSystem;
import me.dyzjct.kura.gui.settingpanel.component.components.*;
import me.dyzjct.kura.setting.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XG42SettingPanel
        extends GuiScreen {
    private final Pane spoilerPane;
    private final HashMap<Category, Pane> categoryPaneMap;
    private final me.dyzjct.kura.gui.settingpanel.Window window;
    private final List<ActionEventListener> onRenderListeners = new ArrayList<>();
    private ParticleSystem particleSystem;
    private Category CurrentCategory;

    public XG42SettingPanel() {
        this.window = new Window(Kura.MOD_NAME, 50, 50, 180, 400);
        me.dyzjct.kura.gui.settingpanel.component.components.ScrollPane contentPane = new me.dyzjct.kura.gui.settingpanel.component.components.ScrollPane(new me.dyzjct.kura.gui.settingpanel.layout.GridLayout(1));
        Pane buttonPane = new Pane(new me.dyzjct.kura.gui.settingpanel.layout.GridLayout(1));
        HashMap<Category, List<Module>> moduleCategoryMap = new HashMap<>();
        this.categoryPaneMap = new HashMap<>();
        for (IModule module : ModuleManager.getAllIModules()) {
            if (!(module instanceof Module)) continue;
            if (!moduleCategoryMap.containsKey(module.category) && !module.category.isHidden()) {
                moduleCategoryMap.put(module.category, new ArrayList<>());
            }
            if (module.category.isHidden()) continue;
            moduleCategoryMap.get(module.category).add((Module) module);
        }
        ArrayList<Spoiler> spoilers = new ArrayList<>();
        ArrayList<Pane> paneList = new ArrayList<>();
        HashMap<Category, Pane> paneMap = new HashMap<>();
        for (Map.Entry<Category, List<Module>> moduleCategoryListEntry : moduleCategoryMap.entrySet()) {
            Pane spoilerPane = new Pane(new me.dyzjct.kura.gui.settingpanel.layout.GridLayout(1));
            for (Module module : moduleCategoryListEntry.getValue()) {
                Pane settingPane = new Pane(new me.dyzjct.kura.gui.settingpanel.layout.GridLayout(4));
                Spoiler spoiler = new Spoiler(module.getName(), 150, settingPane);
                Spoiler addPanelSpoiler = this.updateSpoiler(spoiler);
                paneList.add(addPanelSpoiler.getContentPane());
                spoilers.add(addPanelSpoiler);
                spoilerPane.addComponent(spoiler);
                paneMap.put(moduleCategoryListEntry.getKey(), spoilerPane);
            }
            this.categoryPaneMap.put(moduleCategoryListEntry.getKey(), spoilerPane);
        }
        this.spoilerPane = new Pane(new me.dyzjct.kura.gui.settingpanel.layout.GridLayout(1));
        for (Category moduleCategory : this.categoryPaneMap.keySet()) {
            me.dyzjct.kura.gui.settingpanel.component.components.Button button = new me.dyzjct.kura.gui.settingpanel.component.components.Button(moduleCategory.toString(), 120, 17);
            buttonPane.addComponent(button);
            button.setOnClickListener(() -> this.setCurrentCategory(moduleCategory));
        }
        int maxWidth = Integer.MIN_VALUE;
        for (Pane pane : paneList) {
            maxWidth = Math.max(maxWidth, pane.getWidth() + buttonPane.getWidth());
        }
        this.window.setWidth(28 + maxWidth);
        for (Spoiler spoiler : spoilers) {
            spoiler.preferredWidth = maxWidth - buttonPane.getWidth();
            spoiler.setWidth(maxWidth - buttonPane.getWidth());
        }
        this.spoilerPane.setWidth(maxWidth);
        buttonPane.setWidth(maxWidth);
        ((Pane) contentPane).addComponent(this.spoilerPane);
        ((Pane) contentPane).updateLayout();
        this.window.setContentPane(buttonPane);
        this.window.setSpoilerPane(contentPane);
        if (this.categoryPaneMap.keySet().size() > 0) {
            this.setCurrentCategory(this.categoryPaneMap.keySet().iterator().next());
        }
    }

    private static void lambda$updateSpoiler$11(Slider cb, Setting value) {
        cb.setValue(Double.parseDouble(value.getValue().toString()));
    }

    private static void lambda$updateSpoiler$6(CheckBox cb, Setting value) {
        cb.setSelected(((BooleanSetting) value).getValue());
    }

    public void initGui() {
        this.particleSystem = new ParticleSystem(100);
        if (OpenGlHelper.shadersSupported && Wrapper.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            if (Wrapper.getMinecraft().entityRenderer.getShaderGroup() != null) {
                Wrapper.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            Wrapper.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
        for (Map.Entry<Category, Pane> ff : categoryPaneMap.entrySet()) {
            for (AbstractComponent fff : ff.getValue().getComponent()) {
                if (fff instanceof Spoiler)
                    this.updateSpoiler((Spoiler) fff);
            }
        }
    }

    public void onGuiClosed() {
        if (Wrapper.getMinecraft().entityRenderer.getShaderGroup() != null) {
            Wrapper.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearDepth(1.0);
        GL11.glClear(16640);
        try {
            Point point = Utils.calculateMouseLocation();
            int mX = (int) ((double) point.x);
            int mY = (int) ((double) point.y);
            this.window.mouseReleased(1, mX, mY);
            this.window.mouseReleased(0, mX, mY);
        } catch (Exception ignore) {
        }
    }

    private void setCurrentCategory(Category category) {
        this.spoilerPane.clearComponents();
        this.spoilerPane.addComponent(this.categoryPaneMap.get(category));
        this.CurrentCategory = category;
    }

    private Spoiler updateSpoiler(Spoiler spoiler) {
        String name = spoiler.getTitle();
        IModule module = ModuleManager.getModuleByName(name);
        Pane settingPane = new Pane(new GridLayout(4));
        settingPane.addComponent(new me.dyzjct.kura.gui.settingpanel.component.components.Label("State"));
        CheckBox cb = new CheckBox("Enabled");
        settingPane.addComponent(cb);
        this.onRenderListeners.add(() -> cb.setSelected(module.isEnabled()));
        cb.setListener(val -> {
            if (this.mc.player != null && this.mc.world != null) {
                module.toggle();
                return true;
            }
            return false;
        });
        settingPane.addComponent(new me.dyzjct.kura.gui.settingpanel.component.components.Label("Keybind"));
        KeybindButton kb = new KeybindButton((Module) module);
        settingPane.addComponent(kb);
        this.onRenderListeners.add(() -> kb.setValue(module.getBind()));
        kb.setListener(val -> {
            module.setBind(val);
            return true;
        });
        if (!module.getSettingList().isEmpty()) {
            for (Setting value : module.getSettingList()) {
                AbstractComponent component;
                if (!value.visible()) continue;

                settingPane.addComponent(new me.dyzjct.kura.gui.settingpanel.component.components.Label(value.getName()));
                if (value instanceof BooleanSetting) {
                    component = new CheckBox(value.getName());
                    settingPane.addComponent(component);
                    ((CheckBox) component).setListener(newValue -> {
                        value.setValue(newValue);
                        return true;
                    });
                    this.onRenderListeners.add(() -> XG42SettingPanel.lambda$updateSpoiler$6((CheckBox) component, value));
                } else if (value instanceof ModeSetting) {
                    ComboBox cb3 = new ComboBox(((ModeSetting) value).getModesAsStrings(),
                            ((ModeSetting) value).getIndexEnum((Enum) value.getValue()));
                    settingPane.addComponent(cb3);
                    cb3.setListener(object -> {
                        ((ModeSetting) value).setValueByIndex(object);
                        return true;
                    });
                    this.onRenderListeners.add(() -> cb3.setSelectedIndex((ModeSetting) value));
                } else if (value instanceof IntegerSetting || value instanceof FloatSetting || value instanceof DoubleSetting) {
                    Slider.NumberType type = Slider.NumberType.DECIMAL;
                    double max;
                    double min;

                    //Set Min Max
                    {
                        if (value instanceof IntegerSetting) {
                            max = Double.parseDouble(((IntegerSetting) value).getMax().toString());
                            min = Double.parseDouble(((IntegerSetting) value).getMin().toString());
                        } else if (value instanceof FloatSetting) {
                            max = Double.parseDouble(((FloatSetting) value).getMax().toString());
                            min = Double.parseDouble(((FloatSetting) value).getMin().toString());
                        } else if (value instanceof DoubleSetting) {
                            max = Double.parseDouble(((DoubleSetting) value).getMax().toString());
                            min = Double.parseDouble(((DoubleSetting) value).getMin().toString());
                        } else {
                            max = Double.parseDouble(String.valueOf(Integer.MAX_VALUE));
                            min = Double.parseDouble(String.valueOf(Integer.MAX_VALUE));
                        }
                    }

                    //Set Value Type
                    {
                        if (value.getValue() instanceof Integer) {
                            type = Slider.NumberType.INTEGER;
                        } else if (value.getValue() instanceof Long) {
                            type = Slider.NumberType.TIME;
                        } else if (value.getValue() instanceof Float && (int) min == 0 && (int) max == 100) {
                            type = Slider.NumberType.PERCENT;
                        }
                    }
                    double NumberR = Double.parseDouble(value.getValue().toString());
                    component = new Slider(NumberR, min, max, type);
                    settingPane.addComponent(component);
                    ((Slider) component).setListener(val -> {
                        if (value.getValue() instanceof Integer) {
                            value.setValue(val.intValue());
                        }
                        if (value.getValue() instanceof Float) {
                            value.setValue(val.floatValue());
                        }
                        if (value.getValue() instanceof Long) {
                            value.setValue(val.longValue());
                        }
                        if (value.getValue() instanceof Double) {
                            value.setValue(val.doubleValue());
                        }
                        return true;
                    });
                    this.onRenderListeners.add(() -> XG42SettingPanel.lambda$updateSpoiler$11((Slider) component, value));
                } else if (value instanceof StringSetting) {
                    me.dyzjct.kura.gui.settingpanel.component.components.TextField tf = new TextField(((StringSetting) value).getValue());
                    settingPane.addComponent(tf);
                    tf.setListener(newValue -> {
                        value.setValue(String.valueOf(newValue));
                        return true;
                    });
                    this.onRenderListeners.add(() -> tf.setValue(((StringSetting) value).getValue()));
                } else if (value instanceof ColorSetting) {
                    ColorSlider cs = new ColorSlider((ColorSetting) value);
                    settingPane.addComponent(cs);
                    this.onRenderListeners.add(() -> cs.setColor(((ColorSetting) value).getValue()));
                } else {
                    settingPane.addComponent(new Label("Cannot Certification This Setting Pls Report To Dev!"));
                }
            }
        }
        spoiler.setContentPane(settingPane);
        return spoiler;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        if (UserValueChangeListener.isValueChange) {
            Pane i = this.categoryPaneMap.get(this.CurrentCategory);
            for (AbstractComponent ff : i.getComponent()) {
                this.updateSpoiler((Spoiler) ff);
            }
            UserValueChangeListener.reset();
        }
        for (ActionEventListener onRenderListener : this.onRenderListeners) {
            onRenderListener.onActionEvent();
        }
        this.particleSystem.tick(10);
        Point point = Utils.calculateMouseLocation();
        int mX = (int) ((double) point.x);
        int mY = (int) ((double) point.y);
        this.window.mouseMoved(mX, mY);
        this.particleSystem.render();
        GL11.glPushMatrix();
        //GL11.glScaled(this.Scala, this.Scala, 0.0);
        this.window.render();
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.window.mouseMoved(mouseX, mouseY);
        this.window.mousePressed(mouseButton, mouseX, mouseY);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.window.mouseMoved(mouseX, mouseY);
        this.window.mouseReleased(state, mouseX, mouseY);
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.window.mouseMoved(mouseX, mouseY);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        this.window.mouseWheel(eventDWheel);
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.window.keyPressed(keyCode, typedChar);
        super.keyTyped(typedChar, keyCode);
    }
}

