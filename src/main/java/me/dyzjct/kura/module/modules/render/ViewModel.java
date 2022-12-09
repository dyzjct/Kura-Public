package me.dyzjct.kura.module.modules.render;

import me.dyzjct.kura.event.events.render.item.RenderItemEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.*;

@Module.Info(name = "ViewModel",category = Category.RENDER)
public class ViewModel extends Module
{
    private static ViewModel INSTANCE;
    public ModeSetting settings;
    public BooleanSetting noEatAnimation;
    public Setting<Double> eatX;
    public Setting<Double> eatY;
    public Setting<Boolean> doBob;
    public Setting<Double> mainX;
    public Setting<Double> mainY;
    public Setting<Double> mainZ;
    public Setting<Double> offX;
    public Setting<Double> offY;
    public Setting<Double> offZ;
    public Setting<Integer> mainRotX;
    public Setting<Integer> mainRotY;
    public Setting<Integer> mainRotZ;
    public Setting<Integer> offRotX;
    public Setting<Integer> offRotY;
    public Setting<Integer> offRotZ;
    public Setting<Double> mainScaleX;
    public Setting<Double> mainScaleY;
    public Setting<Double> mainScaleZ;
    public Setting<Double> offScaleX;
    public Setting<Double> offScaleY;
    public Setting<Double> offScaleZ;

    public ViewModel() {
        this.settings = msetting("Settings", Settings.TRANSLATE);
        this.noEatAnimation = bsetting("NoEatAnimation", false).m(settings,Settings.TWEAKS);
        this.eatX = dsetting("EatX", 1.0, (-2.0), 5.0).m(settings,Settings.TWEAKS);
        this.eatY = dsetting("EatY", 1.0, (-2.0), 5.0).m(settings,Settings.TWEAKS);
        this.doBob = bsetting("ItemBob", true).m(settings,Settings.TWEAKS);
        this.mainX = dsetting("MainX", 1.2, (-2.0), 4.0).m(settings,Settings.TRANSLATE);
        this.mainY = dsetting("MainY", (-0.95), (-3.0), 3.0).m(settings,Settings.TRANSLATE);
        this.mainZ = dsetting("MainZ", (-1.45), (-5.0), 5.0).m(settings,Settings.TRANSLATE);
        this.offX = dsetting("OffX", 1.2, (-2.0), 4.0).m(settings,Settings.TRANSLATE);
        this.offY = dsetting("OffY", (-0.95), (-3.0), 3.0).m(settings,Settings.TRANSLATE);
        this.offZ = dsetting("OffZ", (-1.45), (-5.0), 5.0).m(settings,Settings.TRANSLATE);
        this.mainRotX = isetting("MainRotationX", 0, (-36), 36).m(settings,Settings.ROTATE);
        this.mainRotY = isetting("MainRotationY", 0, (-36), 36).m(settings,Settings.ROTATE);
        this.mainRotZ = isetting("MainRotationZ", 0, (-36), 36).m(settings,Settings.ROTATE);
        this.offRotX = isetting("OffRotationX", 0, (-36), 36).m(settings,Settings.ROTATE);
        this.offRotY = isetting("OffRotationY", 0, (-36), 36).m(settings,Settings.ROTATE);
        this.offRotZ = isetting("OffRotationZ", 0, (-36), 36).m(settings,Settings.ROTATE);
        this.mainScaleX = dsetting("MainScaleX", 1.0, 0.1, 5.0).m(settings,Settings.SCALE);
        this.mainScaleY = dsetting("MainScaleY", 1.0, 0.1, 5.0).m(settings,Settings.SCALE);
        this.mainScaleZ = dsetting("MainScaleZ", 1.0, 0.1, 5.0).m(settings,Settings.SCALE);
        this.offScaleX = dsetting("OffScaleX", 1.0, 0.1, 5.0).m(settings,Settings.SCALE);
        this.offScaleY = dsetting("OffScaleY", 1.0, 0.1, 5.0).m(settings,Settings.SCALE);
        this.offScaleZ = dsetting("OffScaleZ", 1.0, 0.1, 5.0).m(settings,Settings.SCALE);
        this.setInstance();
    }

    public static ViewModel getInstance() {
        if (ViewModel.INSTANCE == null) {
            ViewModel.INSTANCE = new ViewModel();
        }
        return ViewModel.INSTANCE;
    }

    private void setInstance() {
        ViewModel.INSTANCE = this;
    }

    @SubscribeEvent
    public void onItemRender(final RenderItemEvent event) {
        event.setMainX((double)this.mainX.getValue());
        event.setMainY((double)this.mainY.getValue());
        event.setMainZ((double)this.mainZ.getValue());
        event.setOffX(-this.offX.getValue());
        event.setOffY((double)this.offY.getValue());
        event.setOffZ((double)this.offZ.getValue());
        event.setMainRotX((double)(this.mainRotX.getValue() * 5));
        event.setMainRotY((double)(this.mainRotY.getValue() * 5));
        event.setMainRotZ((double)(this.mainRotZ.getValue() * 5));
        event.setOffRotX((double)(this.offRotX.getValue() * 5));
        event.setOffRotY((double)(this.offRotY.getValue() * 5));
        event.setOffRotZ((double)(this.offRotZ.getValue() * 5));
        event.setOffHandScaleX((double)this.offScaleX.getValue());
        event.setOffHandScaleY((double)this.offScaleY.getValue());
        event.setOffHandScaleZ((double)this.offScaleZ.getValue());
        event.setMainHandScaleX((double)this.mainScaleX.getValue());
        event.setMainHandScaleY((double)this.mainScaleY.getValue());
        event.setMainHandScaleZ((double)this.mainScaleZ.getValue());
    }

    static {
        ViewModel.INSTANCE = new ViewModel();
    }

    private enum Settings
    {
        TRANSLATE,
        ROTATE,
        SCALE,
        TWEAKS;
    }
}
