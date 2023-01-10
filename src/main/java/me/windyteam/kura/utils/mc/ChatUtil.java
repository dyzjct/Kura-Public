package me.windyteam.kura.utils.mc;

import me.windyteam.kura.Kura;
import me.windyteam.kura.gui.Notification;
import me.windyteam.kura.utils.Utils;
import me.windyteam.kura.utils.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class ChatUtil {

    public static final int DeleteID = 94423;
    public static ArrayList<Notification> notifications = new ArrayList<>();
    public static char SECTIONSIGN = '\u00A7';

    public static String BLACK = SECTIONSIGN + "0";
    public static String DARK_BLUE = SECTIONSIGN + "1";
    public static String DARK_GREEN = SECTIONSIGN + "2";
    public static String DARK_AQUA = SECTIONSIGN + "3";
    public static String DARK_RED = SECTIONSIGN + "4";
    public static String DARK_PURPLE = SECTIONSIGN + "5";
    public static String GOLD = SECTIONSIGN + "6";
    public static String GRAY = SECTIONSIGN + "7";
    public static String DARK_GRAY = SECTIONSIGN + "8";
    public static String BLUE = SECTIONSIGN + "9";
    public static String GREEN = SECTIONSIGN + "a";
    public static String AQUA = SECTIONSIGN + "b";
    public static String RED = SECTIONSIGN + "c";
    public static String LIGHT_PURPLE = SECTIONSIGN + "d";
    public static String YELLOW = SECTIONSIGN + "e";
    public static String WHITE = SECTIONSIGN + "f";
    public static String OBFUSCATED = SECTIONSIGN + "k";
    public static String BOLD = SECTIONSIGN + "l";
    public static String STRIKE_THROUGH = SECTIONSIGN + "m";
    public static String UNDER_LINE = SECTIONSIGN + "n";
    public static String ITALIC = SECTIONSIGN + "o";
    public static String RESET = SECTIONSIGN + "r";

    public static String colorMSG = SECTIONSIGN + "r";
    public static String colorKANJI = SECTIONSIGN + "b";
    public static String colorWarn = SECTIONSIGN + "6" + SECTIONSIGN + "l";
    public static String colorError = SECTIONSIGN + "4" + SECTIONSIGN + "l";
    public static String colorBracket = SECTIONSIGN + "7";

    public static void clear() {
        notifications.clear();
    }

    public static void sendClientMessage(String message, Notification.Type type) {
        if (notifications.size() > 8) {
            notifications.remove(0);
        }
        notifications.add(new Notification(message, type));
    }

    public static void drawNotifications() {
        try {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            double startY = res.getScaledHeight() - 25;
            final double lastY = startY;
            for (int i = 0; i < notifications.size(); i++) {
                Notification not = notifications.get(i);
                if (not.shouldDelete()) {
                    notifications.remove(not);
                    for (int cao = 0; cao > not.width; cao--) {
                        not.animationX = cao - not.width;
                    }
                    startY += not.getHeight() + 3;
                }
                not.draw(startY, lastY);
                for (int cao = 0; cao < not.width; cao++) {
                    not.animationX = cao + not.width;
                }
                startY -= not.getHeight() + 2;
            }
        } catch (Throwable ignored) {

        }
    }

    public static String bracketBuilder(String kanji) {
        return RESET + colorBracket + "[" + RESET + kanji + colorBracket + "] " + RESET;
    }

    public static void printRawMessage(String message) {
        if (Utils.nullCheck()) return;
        ChatMessage(message);
    }

    public static void printMessage(String message) {
        printRawMessage(bracketBuilder(colorKANJI + Kura.KANJI) + RESET + colorMSG + message);
    }

    public static void printWarnMessage(String message) {
        printRawMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorWarn + "WARN") + RESET + colorMSG + message);
    }

    public static void printErrorMessage(String message) {
        printRawMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorError + "ERROR") + RESET + colorMSG + message);
    }

    public static void sendRawMessage(String message) {
        printRawMessage(message);
    }

    public static void sendMessage(String message) {
        printMessage(message);
    }

    public static void sendDisablerDebugMessage(String message){
        sendMessage("[DISABLER] " + message);
    }

    public static void sendMessage(String[] message) {
        for (String msg : message) {
            if (msg != null) {
                sendMessage(msg);
            }
        }
    }

    public static void sendErrorMessage(String message) {
        printErrorMessage(message);
    }

    public static void sendWarnMessage(String message) {
        printWarnMessage(message);
    }

    public static void sendServerMessage(String message) {
        if (Minecraft.getMinecraft().player != null) {
            Wrapper.getPlayer().connection.sendPacket(new CPacketChatMessage(message));
        } else {
            LogWrapper.warning("Could not send server message: \"" + message + "\"");
        }
    }

    @SideOnly(Side.CLIENT)
    public static void sendSpamlessMessage(String message) {
        if (Utils.nullCheck()) return;
        final GuiNewChat chat = Wrapper.mc.ingameGUI.getChatGUI();
        chat.printChatMessageWithOptionalDeletion(new TextComponentString(message), DeleteID);
    }

    @SideOnly(Side.CLIENT)
    public static void sendSpamlessMessage(int messageID, String message) {
        if (Utils.nullCheck()) return;
        final GuiNewChat chat = Wrapper.mc.ingameGUI.getChatGUI();
        chat.printChatMessageWithOptionalDeletion(new TextComponentString(message), messageID);
    }

    public static void ChatMessage(String message) {
        Wrapper.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
    }

    public static void sendNoSpamErrorMessage(String message) {
        sendNoSpamRawChatMessage(SECTIONSIGN + "7[" + SECTIONSIGN + "4" + SECTIONSIGN + "lERROR" + SECTIONSIGN + "7] " + SECTIONSIGN + "r" + message);
    }

    public static void sendNoSpamErrorMessage(String message, int messageID) {
        sendNoSpamRawChatMessage(SECTIONSIGN + "7[" + SECTIONSIGN + "4" + SECTIONSIGN + "lERROR" + SECTIONSIGN + "7] " + SECTIONSIGN + "r" + message, messageID);
    }

    public static void sendNoSpamRawChatMessage(String message) {
        sendSpamlessMessage(message);
    }

    public static void sendNoSpamRawChatMessage(String message, int messageID) {
        sendSpamlessMessage(messageID, message);
    }

    public static class NoSpam {
        public static void sendMessage(String message, int messageID) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + RESET + colorMSG + message, messageID);
        }

        public static void sendMessage(String message) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + RESET + colorMSG + message);
        }

        public static void sendMessage(String[] messages) {
            sendMessage("");
            for (String s : messages) sendRawChatMessage(s);
        }

        public static void sendErrorMessage(String message) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorError + "ERROR") + RESET + colorMSG + message);
        }

        public static void sendErrorMessage(String message, int messageID) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorError + "ERROR") + RESET + colorMSG + message, messageID);
        }

        public static void sendWarnMessage(String message) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorWarn + "WARN") + RESET + colorMSG + message);
        }

        public static void sendWarnMessage(String message, int messageID) {
            sendRawChatMessage(bracketBuilder(colorKANJI + Kura.KANJI) + bracketBuilder(colorWarn + "WARN") + RESET + colorMSG + message, messageID);
        }

        public static void sendRawChatMessage(String message) {
            sendSpamlessMessage(message);
        }

        public static void sendRawChatMessage(String message, int messageID) {
            sendSpamlessMessage(messageID, message);
        }
    }
}
