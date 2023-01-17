package me.windyteam.kura.module.modules.chat

import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.manager.FileManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.BooleanSetting
import me.windyteam.kura.setting.IntegerSetting
import me.windyteam.kura.utils.TimerUtils
import me.windyteam.kura.utils.mc.ChatUtil
import me.windyteam.kura.utils.math.RandomUtil
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random

@Module.Info(name = "Spamer", category = Category.CHAT)
class Spammer : Module() {
    private val delay: IntegerSetting = isetting("Delay", 350, 0, 5000)
    private val randomtxt: BooleanSetting = bsetting("RandomText", false)
    private val randomamount: IntegerSetting = isetting("RandomAmount", 2, 1, 10).b(randomtxt)
    private val sendFriend: BooleanSetting = bsetting("SendToFriend", false)
    private val debug: BooleanSetting = bsetting("Debug", false)
    private val delayTimer: TimerUtils = TimerUtils()
    private val clearTimer: TimerUtils = TimerUtils()
    private val playerList: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()
    val file = File(FileManager.SPAMMER)

    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        delayTimer.reset()
        clearTimer.reset()
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            ChatUtil.sendMessage("Generated A New File!")
            disable()
            return
        }
    }

    override fun onUpdate() {
        if (fullNullCheck()) {
            return
        }
        try {
            if (clearTimer.passed(30000)) {
                playerList.clear()
                clearTimer.reset()
            }
            mc.connection?.playerInfoMap?.forEach {
                if (!it.gameProfile.name.equals(mc.player.name)) {
                    if (!sendFriend.value) {
                        if (!FriendManager.isFriend(it.gameProfile.name)) {
                            if (!playerList.contains(it.gameProfile.name)) {
                                playerList.add(it.gameProfile.name)
                            }
                        }
                    } else {
                        if (!playerList.contains(it.gameProfile.name)) {
                            playerList.add(it.gameProfile.name)
                        }
                    }
                }
            }
            if (file.exists()) {
                val br = BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8))
                var st: String
                while (br.readLine().also { st = it } != null) {
                    if (delayTimer.passed(delay.value)) {
                        val p = playerList[Random.nextInt(playerList.size)]
                        val msg = st.replace("!object", p)
                        mc.player.sendChatMessage(
                            if (randomtxt.value) {
                                "$msg " + RandomUtil.randomString(1, randomamount.value)
                            } else {
                                msg
                            }
                        )
                        playerList.remove(p)
                        if (debug.value) {
                            ChatUtil.sendMessage(msg)
                        }
                        delayTimer.reset()
                    }
                }
            } else {
                ChatUtil.sendMessage("Can't Find The File!")
                disable()
                return
            }
        } catch (ignored: Exception) {
            //ChatUtil.sendMessage(e.message)
        }
    }
}