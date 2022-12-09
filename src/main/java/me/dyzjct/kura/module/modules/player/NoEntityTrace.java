package me.dyzjct.kura.module.modules.player;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;

/**
 * Created by 086 on 8/04/2018.
 */
@Module.Info(name = "NoEntityTrace", category = Category.PLAYER, description = "Blocks entities from stopping you from mining")
public class NoEntityTrace extends Module {

    public static NoEntityTrace INSTANCE = new NoEntityTrace();

}
