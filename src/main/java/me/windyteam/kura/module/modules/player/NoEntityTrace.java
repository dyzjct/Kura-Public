package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;

/**
 * Created by 086 on 8/04/2018.
 */
@Module.Info(name = "NoEntityTrace", category = Category.PLAYER, description = "Blocks entities from stopping you from mining")
public class NoEntityTrace extends Module {

    public static NoEntityTrace INSTANCE = new NoEntityTrace();

}
