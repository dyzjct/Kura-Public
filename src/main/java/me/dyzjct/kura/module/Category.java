package me.dyzjct.kura.module;

public enum Category {
    COMBAT("Combat", false),
    MISC("Misc", false),
    MOVEMENT("Movement", false),
    PLAYER("Player", false),
    RENDER("Render", false),
    CHAT("Chat", false),
    CLIENT("Client", false),
    XDDD("Other", false),
    HUD("HUD", true),
    HIDDEN("Hidden", false, true);

    private final String name;
    private final boolean isHUDCategory;
    private final boolean isHidden;

    Category(String name, boolean isHUDCategory) {
        this.name = name;
        this.isHUDCategory = isHUDCategory;
        this.isHidden = false;
    }

    Category(String name, boolean isHUDCategory, boolean isHidden) {
        this.name = name;
        this.isHUDCategory = isHUDCategory;
        this.isHidden = isHidden;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public boolean isHUD() {
        return this.isHUDCategory;
    }

    public String getName() {
        return this.name;
    }
}

