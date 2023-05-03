package me.windyteam.kura.utils.other

import me.windyteam.kura.module.modules.combat.CityMiner
import net.minecraft.util.math.BlockPos

fun loadCity(){
//    LoadCity
    CityMiner.cityList.add(BlockPos(1,0,0))
    CityMiner.cityList.add(BlockPos(-1,0,0))
    CityMiner.cityList.add(BlockPos(0,0,1))
    CityMiner.cityList.add(BlockPos(0,0,-1))
//    LoadAnti
    CityMiner.antiList.add(BlockPos(2,0,0))
    CityMiner.antiList.add(BlockPos(-2,0,0))
    CityMiner.antiList.add(BlockPos(0,0,2))
    CityMiner.antiList.add(BlockPos(0,0,-2))
}

fun clearCity(){
    CityMiner.cityList.clear()
    CityMiner.antiList.clear()
}