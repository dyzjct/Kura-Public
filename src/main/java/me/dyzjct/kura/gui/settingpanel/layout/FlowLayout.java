package me.dyzjct.kura.gui.settingpanel.layout;

import me.dyzjct.kura.gui.settingpanel.component.AbstractComponent;

import java.util.HashMap;
import java.util.List;

public class FlowLayout
implements ILayoutManager {
    private static final int DEFAULT_VERTICAL_PADDING = 7;
    private static final int DEFAULT_HORIZONTAL_PADDING = 7;
    private int verticalPadding;
    private int horizontalPadding;

    public FlowLayout(int verticalPadding, int horizontalPadding) {
        this.verticalPadding = verticalPadding;
        this.horizontalPadding = horizontalPadding;
    }

    public FlowLayout() {
        this(7, 7);
    }

    public int getVerticalPadding() {
        return this.verticalPadding;
    }

    public void setVerticalPadding(int verticalPadding) {
        this.verticalPadding = verticalPadding;
    }

    public int getHorizontalPadding() {
        return this.horizontalPadding;
    }

    public void setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
    }

    @Override
    public int[] getOptimalDiemension(List<AbstractComponent> components, int maxWidth) {
        int width = -1;
        int height = -1;
        int currX = this.verticalPadding;
        int currY = this.horizontalPadding;
        int maxHeight = -1;
        for (AbstractComponent component : components) {
            int newX = currX + component.getWidth() + this.verticalPadding;
            if (newX > maxWidth) {
                currY += maxHeight;
                maxHeight = -1;
                currX = this.verticalPadding;
                newX = currX + component.getWidth() + this.verticalPadding;
            }
            if (component.getHeight() + this.horizontalPadding > maxHeight) {
                maxHeight = component.getHeight() + this.horizontalPadding;
            }
            width = Math.max(width, newX);
            height = Math.max(height, currY + component.getHeight() + this.horizontalPadding);
            currX = newX;
        }
        return new int[]{width, height};
    }

    @Override
    public Layout buildLayout(List<AbstractComponent> components, int width, int height) {
        HashMap<AbstractComponent, int[]> map = new HashMap<AbstractComponent, int[]>();
        int currX = this.verticalPadding;
        int currY = this.horizontalPadding;
        int maxHeight = -1;
        for (AbstractComponent component : components) {
            int newX = currX + component.getWidth() + this.verticalPadding;
            if (newX > width) {
                currY += maxHeight;
                maxHeight = -1;
                currX = this.verticalPadding;
                newX = currX + component.getWidth() + this.verticalPadding;
            }
            if (component.getHeight() + this.horizontalPadding > maxHeight) {
                maxHeight = component.getHeight() + this.horizontalPadding;
            }
            map.put(component, new int[]{currX, currY});
            currX = newX;
        }
        return new Layout(map, map.entrySet().stream().map(entry -> ((int[])entry.getValue())[1] + ((AbstractComponent)entry.getKey()).getHeight()).max(Integer::compareTo).orElse(0), map.entrySet().stream().map(entry -> ((int[])entry.getValue())[0] + ((AbstractComponent)entry.getKey()).getWidth()).max(Integer::compareTo).orElse(0));
    }
}

