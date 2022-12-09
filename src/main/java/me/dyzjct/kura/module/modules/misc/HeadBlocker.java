package me.dyzjct.kura.module.modules.misc;


import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.movement.Step;
import me.dyzjct.kura.setting.BooleanSetting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

@Module.Info(name = "HeadBlocker",category = Category.MISC)
public class HeadBlocker
        extends Module {
//    private final BooleanSetting GAYAntiCity = bsetting("AntiFeetBreak", true);
    private final BooleanSetting AntiCity = bsetting("AntiHoleMine", true);
    private final BooleanSetting checkstep = bsetting("stepdisable",true);
//    private final BooleanSetting MikuAntiCity = bsetting("SMAntiMine", false);
    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (ModuleManager.getModuleByClass(Step.class).isEnabled()&&checkstep.getValue()){
            return;
        }
        BlockPos pos = new BlockPos(HeadBlocker.mc.player.posX, HeadBlocker.mc.player.posY, HeadBlocker.mc.player.posZ);
        if (mc.player.onGround){
            if (ModuleManager.getModuleByClass(Step.class).isDisabled()&&this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK){
//                if (this.MikuAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiMine.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiMine.class).enable();
//                    }
//                }
                if (this.AntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
                    if (ModuleManager.getModuleByClass(AntiHoleMine.class).isDisabled()) {
                        ModuleManager.getModuleByClass(AntiHoleMine.class).enable();
                    }
                }
//                if (this.GAYAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiFeetBreak.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiFeetBreak.class).enable();
//                    }
//                }
            }
//          1
            if (ModuleManager.getModuleByClass(Step.class).isDisabled()&&this.getBlock(pos.add(-1, 1, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 1, 1)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK){
//                if (this.MikuAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiMine.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiMine.class).enable();
//                    }
//                }
                if (this.AntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
                    if (ModuleManager.getModuleByClass(AntiHoleMine.class).isDisabled()) {
                        ModuleManager.getModuleByClass(AntiHoleMine.class).enable();
                    }
                }
//                if (this.GAYAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiFeetBreak.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiFeetBreak.class).enable();
//                    }
//                }
            }
//          2
            if (ModuleManager.getModuleByClass(Step.class).isDisabled()&&this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(-1, 1, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 1, -1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK){
//                if (this.MikuAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiMine.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiMine.class).enable();
//                    }
//                }
                if (this.AntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
                    if (ModuleManager.getModuleByClass(AntiHoleMine.class).isDisabled()) {
                        ModuleManager.getModuleByClass(AntiHoleMine.class).enable();
                    }
                }
//                if (this.GAYAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiFeetBreak.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiFeetBreak.class).enable();
//                    }
//                }
            }
//          3
            if (ModuleManager.getModuleByClass(Step.class).isDisabled()&&this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(1, 1, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 1, -1)).getBlock() == Blocks.BEDROCK){
//                if (this.MikuAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiMine.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiMine.class).enable();
//                    }
//                }
                if (this.AntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
                    if (ModuleManager.getModuleByClass(AntiHoleMine.class).isDisabled()) {
                        ModuleManager.getModuleByClass(AntiHoleMine.class).enable();
                    }
                }
//                if (this.GAYAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiFeetBreak.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiFeetBreak.class).enable();
//                    }
//                }
            }
//          4
            if (ModuleManager.getModuleByClass(Step.class).isDisabled()&&this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(1, 1, 0)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK && this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN | this.getBlock(pos.add(0, 1, -1)).getBlock() == Blocks.BEDROCK){
//                if (this.MikuAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiMine.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiMine.class).enable();
//                    }
//                }
                if (this.AntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
                    if (ModuleManager.getModuleByClass(AntiHoleMine.class).isDisabled()) {
                        ModuleManager.getModuleByClass(AntiHoleMine.class).enable();
                    }
                }
//                if (this.GAYAntiCity.getValue()&&ModuleManager.getModuleByClass(Step.class).isDisabled()){
//                    if (ModuleManager.getModuleByClass(AntiFeetBreak.class).isDisabled()) {
//                        ModuleManager.getModuleByClass(AntiFeetBreak.class).enable();
//                    }
//                }
            }
        }
    }


    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
//        if (this.MikuAntiCity.getValue()){
//            if (ModuleManager.getModuleByClass(AntiMine.class).isEnabled()) {
//                ModuleManager.getModuleByClass(AntiMine.class).disable();
//            }
//            if (ModuleManager.getModuleByClass(Step.class).isEnabled()) {
//                ModuleManager.getModuleByClass(AntiMine.class).disable();
//            }
//        }
        if (this.AntiCity.getValue()){
            if (ModuleManager.getModuleByClass(AntiHoleMine.class).isEnabled()) {
                ModuleManager.getModuleByClass(AntiHoleMine.class).disable();
            }
            if (ModuleManager.getModuleByClass(Step.class).isEnabled()) {
                ModuleManager.getModuleByClass(AntiHoleMine.class).disable();
            }
        }
//        if (this.GAYAntiCity.getValue()){
//            if (ModuleManager.getModuleByClass(AntiFeetBreak.class).isEnabled()) {
//                ModuleManager.getModuleByClass(AntiFeetBreak.class).disable();
//            }
//            if (ModuleManager.getModuleByClass(Step.class).isEnabled()) {
//                ModuleManager.getModuleByClass(AntiFeetBreak.class).disable();
//            }
//        }
    }

    private IBlockState getBlock(BlockPos block) {
        return HeadBlocker.mc.world.getBlockState(block);
    }
}

