package me.windyteam.kura.module.modules.misc;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;

import java.awt.*;
import java.net.URI;

@Module.Info(name = "AutoPorn", category = Category.MISC, description = "null")
public class AutoPorn extends Module {
    public void onEnable() {
        try {
            Desktop.getDesktop().browse(URI.create("https://pornhub.com"));
            Desktop.getDesktop().browse(URI.create("https://xvideos.com"));
            Desktop.getDesktop().browse(URI.create("https://xhamster.com"));
            Desktop.getDesktop().browse(URI.create("https://cartoon-sex.tv/hentai-porn/"));
            Desktop.getDesktop().browse(URI.create("https://hentaihaven.xxx/"));
        } catch (Exception ignored) {
        }
    }
}
