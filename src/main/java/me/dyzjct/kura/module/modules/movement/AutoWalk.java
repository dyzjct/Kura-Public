package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "AutoWalk",category = Category.MOVEMENT)
public class AutoWalk
        extends Module {

    @SubscribeEvent
    public void onUpdateInput(InputUpdateEvent event) {
        event.getMovementInput().moveForward = 1.0f;
    }
}

