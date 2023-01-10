package me.windyteam.kura.command.commands.module;

import me.windyteam.kura.command.Command;
import me.windyteam.kura.command.syntax.ChunkBuilder;
import me.windyteam.kura.command.syntax.parsers.ModuleParser;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.*;
import me.windyteam.kura.utils.mc.ChatUtil;

import java.util.Optional;
import java.util.stream.Collectors;

public class SetCommand extends Command {

    public SetCommand() {
        super("set", new ChunkBuilder().append("module", true, new ModuleParser()).append("setting", true).append("value", true).build());
        this.setDescription("Change the setting of a certain module");
    }

    @Override
    public void call(String[] args2) {
        if (args2[0] == null) {
            ChatUtil.NoSpam.sendWarnMessage("Please specify a module!");
            return;
        }
        IModule m = ModuleManager.getModuleByName(args2[0]);
        if (m == null) {
            ChatUtil.NoSpam.sendErrorMessage("Unknown module §b" + args2[0] + "§r!");
            return;
        }
        if (args2[1] == null) {
            String settings = String.join(", ", m.getSettingList().stream().map(setting -> setting.getName()).collect(Collectors.toList()));
            if (settings.isEmpty()) {
                ChatUtil.NoSpam.sendErrorMessage("Module §b" + m.getName() + "§r has no settings.");
            } else {
                ChatUtil.sendMessage(new String[]{"Please specify a setting! Choose one of the following:", settings});
            }
            return;
        }
        Optional<Setting> optionalSetting = m.getSettingList().stream().filter(setting1 -> setting1.getName().equalsIgnoreCase(args2[1])).findFirst();
        if (!optionalSetting.isPresent()) {
            ChatUtil.NoSpam.sendErrorMessage("Unknown setting §b" + args2[1] + "§r in §b" + m.getName() + "§r!");
            return;
        }
        Setting setting2 = optionalSetting.get();
        if (args2[2] == null) {
            ChatUtil.NoSpam.sendWarnMessage("§b" + setting2.getName() + "§r is a §3" + setting2.getClass().getSimpleName() + "§r. Its current value is §3" + setting2.getValue().toString());
            return;
        }
        try {
            String arg2 = args2[2];
            if (setting2.getClass().getSimpleName().equals("EnumSetting")) {
                arg2 = arg2.toUpperCase();
            }
            if (setting2 instanceof BooleanSetting) {
                setting2.setValue(Boolean.parseBoolean(arg2));
            } else if (setting2 instanceof DoubleSetting) {
                setting2.setValue(Double.parseDouble(arg2));
            } else if (setting2 instanceof FloatSetting) {
                setting2.setValue(Float.valueOf(Float.parseFloat(arg2)));
            } else if (setting2 instanceof IntegerSetting) {
                setting2.setValue(Integer.parseInt(arg2));
            } else if (setting2 instanceof ModeSetting) {
                ((ModeSetting) setting2).setValueByString(arg2);
            } else if (setting2 instanceof StringSetting) {
                setting2.setValue(arg2);
            }
            ChatUtil.NoSpam.sendWarnMessage("Set §b" + setting2.getName() + "§r to §3" + arg2 + "§r.");
        } catch (Exception e) {
            e.printStackTrace();
            ChatUtil.NoSpam.sendErrorMessage("Unable to set value! §6" + e.getMessage());
        }
    }
}

