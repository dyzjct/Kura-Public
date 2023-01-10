
package me.windyteam.kura.module.modules.movement;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;

@Module.Info(name = "EntityControl", category = Category.MOVEMENT)
public class EntityControl
extends Module {
    public static EntityControl INSTANCE;

    public EntityControl() {
        INSTANCE = this;
    }
}

