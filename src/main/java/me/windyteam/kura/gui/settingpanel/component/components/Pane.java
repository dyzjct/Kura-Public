package me.windyteam.kura.gui.settingpanel.component.components;

import me.windyteam.kura.gui.settingpanel.component.AbstractComponent;
import me.windyteam.kura.gui.settingpanel.layout.ILayoutManager;
import me.windyteam.kura.gui.settingpanel.layout.Layout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pane
extends AbstractComponent {
    protected List<AbstractComponent> components = new ArrayList<AbstractComponent>();
    protected Map<AbstractComponent, int[]> componentLocations = new HashMap<AbstractComponent, int[]>();
    protected Layout layout;
    private ILayoutManager layoutManager;

    public Pane(ILayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void render() {
        if (this.isSizeChanged()) {
            this.updateLayout();
            this.resetSizeChanged();
        }
        this.updateComponentLocation();
        for (AbstractComponent component : this.components) {
            component.render();
        }
    }

    @Override
    public boolean isSizeChanged() {
        for (AbstractComponent component : this.components) {
            if (!component.isSizeChanged()) continue;
            return true;
        }
        return super.isSizeChanged();
    }

    private void resetSizeChanged() {
        for (AbstractComponent component : this.components) {
            component.setSizeChanged(false);
        }
    }

    protected void updateComponentLocation() {
        for (AbstractComponent component : this.components) {
            int[] ints = this.componentLocations.get(component);
            if (ints == null) {
                this.updateLayout();
                this.updateComponentLocation();
                return;
            }
            component.setX(this.getX() + ints[0]);
            component.setY(this.getY() + ints[1]);
        }
    }

    public void updateLayout() {
        this.updateLayout(this.getWidth(), this.getHeight(), true);
    }

    protected void updateLayout(int width, int height, boolean changeHeight) {
        this.layout = this.layoutManager.buildLayout(this.components, width, height);
        this.componentLocations = this.layout.getComponentLocations();
        if (changeHeight) {
            this.setHeight(this.layout.getMaxHeight());
        }
    }

    public void addComponent(AbstractComponent component) {
        this.components.add(component);
        this.updateLayout(super.getWidth(), super.getHeight(), true);
    }

    public List<AbstractComponent> getComponent() {
        return this.components;
    }

    public void removeComponent(AbstractComponent component) {
        this.components.remove(component);
        this.updateLayout(super.getWidth(), super.getHeight(), true);
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        boolean[] consumed = new boolean[]{false};
        this.components.stream().sorted(Comparator.comparingInt(AbstractComponent::getEventPriority)).forEach(component -> {
            if (!consumed[0] && component.mouseMove(x, y, offscreen)) {
                consumed[0] = true;
            }
        });
        return consumed[0];
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        boolean[] consumed = new boolean[]{false};
        this.components.stream().sorted(Comparator.comparingInt(AbstractComponent::getEventPriority)).forEach(component -> {
            if (!consumed[0] && component.mousePressed(button, x, y, offscreen)) {
                consumed[0] = true;
            }
        });
        return consumed[0];
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        boolean[] consumed = new boolean[]{false};
        this.components.stream().sorted(Comparator.comparingInt(AbstractComponent::getEventPriority)).forEach(component -> {
            if (!consumed[0] && component.mouseReleased(button, x, y, offscreen)) {
                consumed[0] = true;
            }
        });
        return consumed[0];
    }

    @Override
    public boolean keyPressed(int key, char c) {
        boolean[] consumed = new boolean[]{false};
        this.components.stream().sorted(Comparator.comparingInt(AbstractComponent::getEventPriority)).forEach(component -> {
            if (!consumed[0] && component.keyPressed(key, c)) {
                consumed[0] = true;
            }
        });
        return consumed[0];
    }

    @Override
    public int getWidth() {
        if (super.getWidth() <= 0) {
            this.updateSize();
        }
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        if (super.getHeight() <= 0) {
            this.updateSize();
        }
        return super.getHeight();
    }

    private void updateSize() {
        for (AbstractComponent component : this.components) {
            if (!(component instanceof Pane)) continue;
            ((Pane)component).updateSize();
        }
        int[] optimalDiemension = this.layoutManager.getOptimalDiemension(this.components, Integer.MAX_VALUE);
        if (super.getWidth() <= 0) {
            this.setWidth(optimalDiemension[0]);
        }
        if (super.getHeight() <= 0) {
            this.setHeight(optimalDiemension[1]);
        }
    }

    public void clearComponents() {
        this.components.clear();
        this.updateLayout(super.getWidth(), super.getHeight(), true);
    }
}

