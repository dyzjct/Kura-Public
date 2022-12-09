package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.player.Timer;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Module.Info(name = "AntiAnvil",category = Category.MISC)
public class AntiAnvil 
        extends Module {
    //hacked by dyzjct
    //Windy Team IS GOD
    private final Setting<Boolean> rotate = bsetting("Rotate", false);
    private final Setting<Boolean> toggle = bsetting("AutoToggle", false);
    private int obsidian = -1;
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private BlockPos startPos;

    private int lol;

    public static boolean isHard(Block block) {
        return block == Blocks.BEDROCK;
    }

    @Override
    public void onEnable() {
        this.startPos = EntityUtil.getRoundedBlockPos((Entity) mc.player);
        this.startPos = EntityUtil.getRoundedBlockPos(AntiAnvil.mc.player);
        lol=0;
    }

    //  ewe
    @Override
    public void onDisable() {
        lol=0;
    }


    @SubscribeEvent
    public void onTick(MotionUpdateEvent.Tick event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
//        ewe
        if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(AntiAnvil.mc.player))) {
            this.toggle();
        }
    }



    @Override
    public void onUpdate() {
        if (AntiAnvil.mc.player == null || AntiAnvil.mc.world == null) {
            return;
        }
        if (ModuleManager.getModuleByClass(AntiMine.class).isDisabled()) {
            ModuleManager.getModuleByClass(AntiAnvil.class).disable();
        }
        this.obsidian = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if (this.obsidian == -1) {
            return;
        }
        BlockPos pos = new BlockPos(AntiAnvil.mc.player.posX, AntiAnvil.mc.player.posY, AntiAnvil.mc.player.posZ);
        if (pos == null) {
            return;
        }
//        MADE BY DYZJCT

        if (this.getBlock(pos.add(1,2,0)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(-1,2,0)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(0,2,1)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(0,2,-1)).getBlock()==Blocks.OBSIDIAN){
            lol=1;
        }
        if (lol==1&&this.getBlock(pos.add(0,4,1)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(0,2,0)).getBlock()==Blocks.AIR){
                if (this.getBlock(pos.add(0,3,1)).getBlock()==Blocks.AIR){
                    if (this.getBlock(pos.add(0,2,1)).getBlock()==Blocks.AIR){
                        if  (this.getBlock(pos.add(0,1,1)).getBlock()==Blocks.AIR){
                            this.perform(pos.add(0,1,1));
                        }
                        this.perform(pos.add(0,2,1));
                    }
                    this.perform(pos.add(0,3,1));
                }
            if (this.getBlock(pos.add(0,3,0)).getBlock()==Blocks.AIR){
                this.perform(pos.add(0,3,0));
            }
        }
        if (lol==1&&this.getBlock(pos.add(0,4,1)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(0,3,0)).getBlock()==Blocks.AIR){
                    if (this.getBlock(pos.add(0,2,1)).getBlock()==Blocks.AIR){
                        if  (this.getBlock(pos.add(0,1,1)).getBlock()==Blocks.AIR){
                            this.perform(pos.add(0,1,1));
                        }
                        this.perform(pos.add(0,2,1));
                    }
            if (this.getBlock(pos.add(0,2,0)).getBlock()==Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
        }
        if (this.getBlock(pos.add(0,5,1)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(0,2,0)).getBlock()!=Blocks.OBSIDIAN){
                if (this.getBlock(pos.add(0,3,1)).getBlock()==Blocks.AIR){
                    if (this.getBlock(pos.add(0,2,1)).getBlock()==Blocks.AIR){
                        if  (this.getBlock(pos.add(0,1,1)).getBlock()==Blocks.AIR){
                            this.perform(pos.add(0,1,1));
                        }
                        this.perform(pos.add(0,2,1));
                    }
                    this.perform(pos.add(0,3,1));
                }
            if (this.getBlock(pos.add(0,3,0)).getBlock()==Blocks.AIR){
                this.perform(pos.add(0,3,0));
            }
        }
        if (this.getBlock(pos.add(0,5,1)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(0,3,0)).getBlock()!=Blocks.OBSIDIAN){
                    if (this.getBlock(pos.add(0,2,1)).getBlock()==Blocks.AIR){
                        if  (this.getBlock(pos.add(0,1,1)).getBlock()==Blocks.AIR){
                            this.perform(pos.add(0,1,1));
                        }
                        this.perform(pos.add(0,2,1));
                    }
            if (this.getBlock(pos.add(0,2,0)).getBlock()==Blocks.AIR){
                this.perform(pos.add(0,2,0));
            }
        }
//        if (this.getBlock(pos.add(1,2,0)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(-1,2,0)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(0,2,1)).getBlock()==Blocks.OBSIDIAN&&this.getBlock(pos.add(0,2,-1)).getBlock()==Blocks.OBSIDIAN){
//            if (this.getBlock(pos.add(0,2,1)).getBlock()==Blocks.AIR){
//                if  (this.getBlock(pos.add(0,1,1)).getBlock()==Blocks.AIR){
//                    this.perform(pos.add(0,1,1));
//                }
//                this.perform(pos.add(0,2,1));
//             }
//            if (this.getBlock(pos.add(0,2,0)).getBlock()==Blocks.AIR){
//                this.perform(pos.add(0,2,0));
//            }
//        }
    }


    private void switchToSlot(int slot) {
        AntiAnvil.mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(slot));
        AntiAnvil.mc.player.inventory.currentItem = slot;
        AntiAnvil.mc.playerController.updateController();
    }

    private IBlockState getBlock(BlockPos block) {
        return AntiAnvil.mc.world.getBlockState(block);
    }


    private void perform(BlockPos pos) {
        int old = AntiAnvil.mc.player.inventory.currentItem;
        this.switchToSlot(this.obsidian);
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false);
        this.switchToSlot(old);
    }
}