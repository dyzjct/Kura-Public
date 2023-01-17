package me.windyteam.kura.module.modules.chat

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.utils.Timer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Info(name = "AutoCNM", category = Category.CHAT, description = "Fuck u 4i04")
class AutoCNM : Module(){
    private var fuckerdelay = isetting("FuckDelay",500,0,10000)
    private var fucker = mutableListOf<String>()
    private var timer = Timer()

    override fun onEnable() {
        fucker.add("老子操你妈逼的窝囊废辱华玩意")
        fucker.add("你家祖坟是不是被鬼子内射了")
        fucker.add("你脑中是不是给狗屎堵塞了")
        fucker.add("你他妈就好比一条野犬找到了生活的希望")
        fucker.add("你写的垃圾Moongod就和窝囊废一样")
        fucker.add("你以为你什么东西啊")
        fucker.add("老子轻轻松松给你女朋友给你亲妈操了")
        fucker.add("你懂不懂 你以为你在游戏里面非常牛逼其实现实世界就是一个窝囊废吗")
        fucker.add("老子看到你就和看到路边的一坨狗屎一样恶心")
    }
    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent){
        if (mc.player == null || mc.world == null) return
        for (i in fucker){
            if (timer.passedMs(fuckerdelay.value.toLong())){
                mc.player.sendChatMessage(i)
            }
        }
    }

    override fun disable() {
        fucker.clear()
    }
}