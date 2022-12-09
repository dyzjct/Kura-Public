
package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;

@Module.Info(name = "EntityControl", category = Category.MOVEMENT)
public class EntityControl
extends Module {
    public static EntityControl INSTANCE;

    public EntityControl() {
        INSTANCE = this;
    }
}

