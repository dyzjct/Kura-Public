package me.windyteam.kura.command.commands;

import me.windyteam.kura.Kura;
import me.windyteam.kura.command.Command;
import me.windyteam.kura.command.syntax.SyntaxChunk;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.utils.mc.ChatUtil;

import java.util.Arrays;

public class HelpCommand
        extends Command {
    private static final Subject[] subjects = new Subject[]{new Subject(new String[]{"type", "int", "boolean", "double", "float"}, new String[]{"Every module has a value, and that value is always of a certain §btype.\n", "These types are displayed in kami as the ones java use. They mean the following:", "§bboolean§r: Enabled or not. Values §3true/false", "§bfloat§r: A number with a decimal point", "§bdouble§r: Like a float, but a more accurate decimal point", "§bint§r: A number with no decimal point"})};
    private static String subjectsList = "";

    public HelpCommand() {
        super("help", SyntaxChunk.EMPTY);
        this.setDescription("Delivers help on certain subjects. Use b-help subjects§r for a list.");
    }

    @Override
    public void call(String[] args2) {
        if (args2[0] == null) {
            ChatUtil.sendMessage(new String[]{Kura.MOD_NAME + " ", "commands§7 to view all available commands", "bind <module> <key>§7 to bind mods", "§7Press §r" + ModuleManager.getModuleByName("ClickGUI").getBind() + "§7 to open GUI", "prefix <prefix>§r to change the command prefix.", "help <subjects:[subject]> §r for more help."});
        } else {
            String subject = args2[0];
            if (subject.equals("subjects")) {
                ChatUtil.NoSpam.sendMessage("Subjects: " + subjectsList);
            } else {
                Subject subject1 = Arrays.stream(subjects).filter(subject2 -> {
                    for (String name : subject2.names) {
                        if (!name.equalsIgnoreCase(subject)) continue;
                        return true;
                    }
                    return false;
                }).findFirst().orElse(null);
                if (subject1 == null) {
                    ChatUtil.NoSpam.sendWarnMessage("No help found for §b" + args2[0]);
                    return;
                }
                ChatUtil.sendMessage(subject1.info);
            }
        }
    }

    static {
        for (Subject subject : subjects) {
            subjectsList = subjectsList + subject.names[0] + ", ";
        }
        subjectsList = subjectsList.substring(0, subjectsList.length() - 2);
    }

    private static class Subject {
        String[] names;
        String[] info;

        public Subject(String[] names, String[] info) {
            this.names = names;
            this.info = info;
        }
    }
}

