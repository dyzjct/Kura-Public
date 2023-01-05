package me.dyzjct.kura.event.events.xin

import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AutoQueueEvent : Event() {
    @SubscribeEvent(priority = EventPriority.HIGH)
    fun onReceiveChat(event: ClientChatReceivedEvent) {
        try {
            if (mc.player != null) {
                if (event.message.toString().contains(mc.player.name)) {
                    return
                }
            }
            if (event.message.toString().contains("无限水至少需要几格空间?")) {
                if (event.message.toString().contains("A.3")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.3")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.3")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("定位末地遗迹至少需要几颗末影之眼?")) {
                if (event.message.toString().contains("A.0")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.0")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.0")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("凋零死后会掉落什么?")) {
                if (event.message.toString().contains("A.下界之星")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.下界之星")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.下界之星")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("爬行者被闪电击中后会变成什么?")) {
                if (event.message.toString().contains("A.高压爬行者")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.高压爬行者")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.高压爬行者")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("猪被闪电击中后会变成什么?")) {
                if (event.message.toString().contains("A.僵尸猪人")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.僵尸猪人")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.僵尸猪人")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("羊驼会主动攻击人吗?")) {
                if (event.message.toString().contains("A.不会")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.不会")) {
                    mc.player.sendChatMessage("B")
                }
            } else if (event.message.toString().contains("红石火把信号有几格?")) {
                if (event.message.toString().contains("A.15")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.15")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.15")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("小箱子能存储多少物品?")) {
                if (event.message.toString().contains("A.27")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.27")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.27")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("钻石的物品数字ID是多少?")) {
                if (event.message.toString().contains("A.264")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.264")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.264")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("南瓜的生长是否需要水?")) {
                if (event.message.toString().contains("A.不需要")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.不需要")) {
                    mc.player.sendChatMessage("B")
                }
            } else if (event.message.toString().contains("大箱子能存储多少格物品?")) {
                if (event.message.toString().contains("A.54")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.54")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.54")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("本服务器开服年份?")) {
                if (event.message.toString().contains("A.2020")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.2020")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.2020")) {
                    mc.player.sendChatMessage("C")
                }
            } else if (event.message.toString().contains("挖掘速度最快的稿子是什么?")) {
                if (event.message.toString().contains("A.金稿")) {
                    mc.player.sendChatMessage("A")
                } else if (event.message.toString().contains("B.金稿")) {
                    mc.player.sendChatMessage("B")
                } else if (event.message.toString().contains("C.金稿")) {
                    mc.player.sendChatMessage("C")
                }
            }
        } catch (ignored: Exception) {
        }
    }

    companion object {
        var mc = Minecraft.getMinecraft()
    }
}