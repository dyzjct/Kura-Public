package me.windyteam.kura.manager;

import com.google.gson.*;
import me.windyteam.kura.Kura;
import me.windyteam.kura.command.commands.comm.BindCommand;
import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.gui.alt.utils.Alt;
import me.windyteam.kura.gui.alt.utils.AltSystem;
import me.windyteam.kura.gui.clickgui.GUIRender;
import me.windyteam.kura.gui.clickgui.HUDRender;
import me.windyteam.kura.gui.clickgui.Panel;
import me.windyteam.kura.module.HUDModule;
import me.windyteam.kura.module.IModule;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.client.NullHUD;
import me.windyteam.kura.module.modules.client.NullModule;
import me.windyteam.kura.setting.*;
import me.windyteam.kura.utils.EncryptionUtils;
import me.windyteam.kura.utils.other.Friend;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileManager {
    public static FileManager INSTANCE;
    private static final Gson gsonPretty;
    private static final JsonParser jsonParser;
    // Default
    public static final String D = Kura.MOD_NAME;
    public static final String CONFIG_PATH = D + "/config/";
    public static final String NOTEBOT_PATH = D + "/notebot/";
    public static final String BACKGROUND_PATH = D + "/background/";
    public static final String ALT_CONFIG = D + "/" + Kura.MOD_NAME + "-Alt.json";
    public static final String CLIENT_CONFIG = D + "/" + Kura.MOD_NAME + "-Client.json";
    public static final String FRIEND_CONFIG = D + "/" + Kura.MOD_NAME + "-Friend.json";
    public static final String GUI_CONFIG = D + "/" + Kura.MOD_NAME + "-Gui.json";
    public static final String HUD_CONFIG = CONFIG_PATH + "/" + Kura.MOD_NAME + "-HUDModule.json";
    public static final String MODULE_CONFIG = CONFIG_PATH + "/modules/";
    private File CLIENT_FILE;
    private File FRIEND_FILE;
    private File GUI_FILE;
    private File HUD_FILE;
    private File ALT_FILE;
    private final List<File> initializedConfig = new ArrayList<>();

    public FileManager() {
        INSTANCE = this;
        this.init();
    }

    private void init() {
        if (!this.tryLoad()) {
            this.deleteFiles();
        }
        checkPath(new File(NOTEBOT_PATH));
        checkPath(new File(BACKGROUND_PATH));
    }

    public void deleteFiles() {
        try {
            this.initializedConfig.forEach(File::delete);
            Kura.logger.info("All config files deleted successfully!\n");
        } catch (Exception e) {
            Kura.logger.error("Error while deleting config files!");
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        INSTANCE.saveClient();
        INSTANCE.saveFriend();
        INSTANCE.saveGUI();
        INSTANCE.saveHUD();
        INSTANCE.saveModule();
        INSTANCE.saveAlt();
    }

    public static void loadAll() {
        INSTANCE.loadClient();
        INSTANCE.loadFriend();
        INSTANCE.loadGUI();
        INSTANCE.loadHUD();
        INSTANCE.loadModule();
        INSTANCE.loadAlt();
    }

    private void saveClient() {
        try {
            checkFile(CLIENT_FILE);
            JsonObject father = new JsonObject();
            JsonObject stuff = new JsonObject();
            stuff.addProperty("CommandPrefix", Kura.commandPrefix.getValue());
            stuff.addProperty(BindCommand.modifiersEnabled.getName(), BindCommand.modifiersEnabled.getValue());
            father.add("Client", stuff);
            PrintWriter saveJson = new PrintWriter(new FileWriter(CLIENT_CONFIG));
            saveJson.println(gsonPretty.toJson(father));
            saveJson.close();
        } catch (Exception e) {
            Kura.logger.error("Error while saving Client stuff!");
            e.printStackTrace();
        }
    }

    private void saveFriend() {
        try {
            if (!FRIEND_FILE.exists()) {
                FRIEND_FILE.getParentFile().mkdirs();
                try {
                    FRIEND_FILE.createNewFile();
                } catch (Exception ignored) {
                }
            }
            JsonObject father = new JsonObject();
            for (Friend friend : FriendManager.getFriendList()) {
                JsonObject stuff = new JsonObject();
                stuff.addProperty("isFriend", friend.isFriend);
                father.add(friend.name, stuff);
            }
            PrintWriter saveJSon = new PrintWriter(new FileWriter(FRIEND_CONFIG));
            saveJSon.println(gsonPretty.toJson(father));
            saveJSon.close();
        } catch (Exception e) {
            Kura.logger.error("Error while saving friends!");
            e.printStackTrace();
        }
    }

    private void saveGUI() {
        try {
            checkFile(GUI_FILE);
            JsonObject jsonGui;
            JsonObject father = new JsonObject();
            for (Panel panel : GUIRender.panels) {
                jsonGui = new JsonObject();
                jsonGui.addProperty("X", panel.x);
                jsonGui.addProperty("Y", panel.y);
                jsonGui.addProperty("Extended", panel.extended);
                father.add(panel.category.getName(), jsonGui);
            }
            for (Panel panel : HUDRender.getINSTANCE().panels) {
                jsonGui = new JsonObject();
                jsonGui.addProperty("X", panel.x);
                jsonGui.addProperty("Y", panel.y);
                jsonGui.addProperty("Extended", panel.extended);
                father.add(panel.category.getName(), jsonGui);
            }
            PrintWriter saveJSon = new PrintWriter(new FileWriter(GUI_CONFIG));
            saveJSon.println(gsonPretty.toJson(father));
            saveJSon.close();
        } catch (Exception e) {
            Kura.logger.error("Error while saving GUI config!");
            e.printStackTrace();
        }
    }

    private void saveHUD() {
        try {
            checkFile(HUD_FILE);
            JsonObject father = new JsonObject();
            for (IModule module : ModuleManager.getHUDModules()) {
                JsonObject jsonModule = new JsonObject();
                jsonModule.addProperty("Enable", module.toggled);
                jsonModule.addProperty("HUDPosX", module.x);
                jsonModule.addProperty("HUDPosY", module.y);
                jsonModule.addProperty("Bind", module.getBind());
                if (!module.getSettingList().isEmpty()) {
                    for (Setting setting : module.getSettingList()) {
                        if (setting instanceof StringSetting) {
                            jsonModule.addProperty(setting.getName(), (String) setting.getValue());
                        }
                        if (setting instanceof ColorSetting) {
                            jsonModule.addProperty(setting.getName(), ((Color) setting.getValue()).getRGB());
                        }
                        if (setting instanceof BooleanSetting) {
                            jsonModule.addProperty(setting.getName(), (Boolean) setting.getValue());
                        }
                        if (setting instanceof IntegerSetting) {
                            jsonModule.addProperty(setting.getName(), (Integer) setting.getValue());
                        }
                        if (setting instanceof FloatSetting) {
                            jsonModule.addProperty(setting.getName(), (Float) setting.getValue());
                        }
                        if (setting instanceof DoubleSetting) {
                            jsonModule.addProperty(setting.getName(), (Double) setting.getValue());
                        }
                        if (!(setting instanceof ModeSetting)) continue;
                        ModeSetting modeValue = (ModeSetting) setting;
                        jsonModule.addProperty(modeValue.getName(), modeValue.getValueAsString());
                    }
                }
                module.onConfigSave();
                father.add(module.getName(), jsonModule);
            }
            PrintWriter saveJSon = new PrintWriter(new FileWriter(HUD_CONFIG));
            saveJSon.println(gsonPretty.toJson(father));
            saveJSon.close();
        } catch (Exception e) {
            Kura.logger.error("Error while saving HUD config!");
            e.printStackTrace();
        }
    }

    private void saveModule() {
        try {
            JsonObject father = new JsonObject();
            for (IModule module : ModuleManager.getModules()) {
                File MODULE = new File("Kura/config/modules/" + module.getName() + ".json");
                checkFile(MODULE);
                JsonObject jsonModule = new JsonObject();
                jsonModule.addProperty("Enable", module.toggled);
                jsonModule.addProperty("Bind", module.getBind());
                if (!module.getSettingList().isEmpty()) {
                    for (Setting setting : module.getSettingList()) {
                        if (setting instanceof StringSetting) {
                            jsonModule.addProperty(setting.getName(), (String) setting.getValue());
                        }
                        if (setting instanceof ColorSetting) {
                            jsonModule.addProperty(setting.getName(), ((Color) setting.getValue()).getRGB());
                        }
                        if (setting instanceof BooleanSetting) {
                            jsonModule.addProperty(setting.getName(), (Boolean) setting.getValue());
                        }
                        if (setting instanceof IntegerSetting) {
                            jsonModule.addProperty(setting.getName(), (Integer) setting.getValue());
                        }
                        if (setting instanceof FloatSetting) {
                            jsonModule.addProperty(setting.getName(), (Float) setting.getValue());
                        }
                        if (setting instanceof DoubleSetting) {
                            jsonModule.addProperty(setting.getName(), (Double) setting.getValue());
                        }
                        if (!(setting instanceof ModeSetting)) continue;
                        ModeSetting modeValue = (ModeSetting) setting;
                        jsonModule.addProperty(modeValue.getName(), modeValue.getValueAsString());
                    }
                }
                PrintWriter saveJSon = new PrintWriter(new FileWriter(MODULE));
                saveJSon.println(gsonPretty.toJson(jsonModule));
                saveJSon.close();
                module.onConfigSave();
                father.add(module.getName(), jsonModule);
            }
        } catch (Exception e) {
            Kura.logger.error("Error while saving module config!");
            e.printStackTrace();
        }
    }

    private void saveAlt() {
        try {
            checkFile(ALT_FILE);
            JsonObject father = new JsonObject();
            for (Alt alt : AltSystem.getAlts()) {
                JsonObject stuff = new JsonObject();
                stuff.addProperty("Pass", EncryptionUtils.Encrypt(alt.getPassword(), Kura.ALT_Encrypt_Key));
                father.add(alt.getUsername(), stuff);
            }
            PrintWriter saveJSon = new PrintWriter(new FileWriter(ALT_FILE));
            saveJSon.println(gsonPretty.toJson(father));
            saveJSon.close();
        } catch (Exception e) {
            Kura.logger.error("Error while saving alt!");
            e.printStackTrace();
        }
    }

    private void loadClient() {
        if (this.CLIENT_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(this.CLIENT_FILE));
                JsonObject guiJason = (JsonObject) jsonParser.parse(loadJson);
                loadJson.close();
                for (Map.Entry entry : guiJason.entrySet()) {
                    if (!entry.getKey().equals("Client")) continue;
                    JsonObject json = (JsonObject) entry.getValue();
                    this.trySetClient(json);
                }
            } catch (IOException e) {
                Kura.logger.error("Error while loading Client stuff!");
                e.printStackTrace();
            }
        }
    }

    private void loadFriend() {
        if (FRIEND_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(FRIEND_FILE));
                JsonObject friendJson = (JsonObject) jsonParser.parse(loadJson);
                loadJson.close();
                Kura.Companion.getInstance().friendManager.friends.clear();
                for (Map.Entry<String, JsonElement> entry : friendJson.entrySet()) {
                    if (entry.getKey() == null) continue;
                    JsonObject nmsl = (JsonObject) entry.getValue();
                    String name = entry.getKey();
                    boolean isFriend = false;
                    try {
                        isFriend = nmsl.get("isFriend").getAsBoolean();
                    } catch (Exception e) {
                        Kura.logger.error("Can't set friend value for " + name + ", unfriended!");
                    }
                    Kura.Companion.getInstance().friendManager.friends.add(new Friend(name, isFriend));
                }
            } catch (IOException e) {
                Kura.logger.error("Error while loading friends!");
                e.printStackTrace();
            }
        }
    }

    private void loadGUI() {
        if (this.GUI_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(this.GUI_FILE));
                JsonObject guiJson = (JsonObject) jsonParser.parse(loadJson);
                loadJson.close();
                for (Map.Entry entry : guiJson.entrySet()) {
                    Panel panel = GUIRender.getPanelByName((String) entry.getKey());
                    if (panel == null) {
                        panel = HUDRender.getPanelByName((String) entry.getKey());
                    }
                    if (panel == null) continue;
                    JsonObject jsonGui = (JsonObject) entry.getValue();
                    panel.x = jsonGui.get("X").getAsInt();
                    panel.y = jsonGui.get("Y").getAsInt();
                    panel.extended = jsonGui.get("Extended").getAsBoolean();
                }
            } catch (IOException e) {
                Kura.logger.error("Error while loading GUI config!");
                e.printStackTrace();
            }
        }
    }

    private void loadHUD() {
        if (this.HUD_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(this.HUD_FILE));
                JsonObject moduleJason = (JsonObject) jsonParser.parse(loadJson);
                loadJson.close();
                for (Map.Entry entry : moduleJason.entrySet()) {
                    HUDModule module = ModuleManager.getHUDByName((String) entry.getKey());
                    if (module instanceof NullHUD) continue;
                    JsonObject jsonMod = (JsonObject) entry.getValue();
                    boolean enabled = jsonMod.get("Enable").getAsBoolean();
                    if (module.isEnabled() && !enabled) {
                        module.disable();
                    }
                    if (module.isDisabled() && enabled) {
                        module.enable();
                    }
                    module.x = jsonMod.get("HUDPosX").getAsInt();
                    module.y = jsonMod.get("HUDPosY").getAsInt();
                    if (!module.getSettingList().isEmpty()) {
                        this.trySet(module, jsonMod);
                    }
                    module.onConfigLoad();
                    module.setBind(jsonMod.get("Bind").getAsInt());
                }
            } catch (IOException e) {
                Kura.logger.info("Error while loading module config");
                e.printStackTrace();
            }
        }
    }

    public void loadModule() {
        try {
            for (IModule module : ModuleManager.getModules()) {
                File modulefile = new File("Kura/config/modules/" + module.getName() + ".json");
                if (!modulefile.exists()) continue;
                BufferedReader loadJson = new BufferedReader(new FileReader(modulefile));
                JsonObject moduleJason = (JsonObject) jsonParser.parse(loadJson);
                loadJson.close();
                for (Map.Entry ignored : moduleJason.entrySet()) {
                    IModule modul = ModuleManager.getModuleByName(module.getName());
                    if (modul instanceof NullModule) continue;
                    boolean enabled = moduleJason.get("Enable").getAsBoolean();
                    if (modul.isEnabled() && !enabled) {
                        modul.disable();
                    }
                    if (modul.isDisabled() && enabled) {
                        modul.enable();
                    }
                    if (!modul.getSettingList().isEmpty()) {
                        this.trySet(modul, moduleJason);
                    }
                    modul.onConfigLoad();
                    modul.setBind(moduleJason.get("Bind").getAsInt());
                }
            }
        } catch (IOException e) {
            Kura.logger.info("Error while loading module config");
            e.printStackTrace();
        }
    }

    private void loadAlt() {
        if (this.ALT_FILE.exists()) {
            try {
                BufferedReader loadJson = new BufferedReader(new FileReader(this.ALT_FILE));
                JsonObject ALTJson = (JsonObject) jsonParser.parse(loadJson);
                loadJson.close();
                for (Map.Entry<String, JsonElement> entry : ALTJson.entrySet()) {
                    if (entry.getKey() == null) continue;
                    JsonObject jsonalt = (JsonObject) entry.getValue();
                    String pass = "";
                    try {
                        pass = EncryptionUtils.Decrypt(jsonalt.get("Pass").getAsString(), Kura.ALT_Encrypt_Key);
                    } catch (Exception e) {
                        Kura.logger.error("Can't set Password for " + entry.getKey() + "!");
                    }
                    AltSystem.getAlts().add(new Alt(entry.getKey(), pass));
                }
            } catch (IOException e) {
                Kura.logger.error("Error while loading Alt!");
                e.printStackTrace();
            }
        }
    }

    private boolean tryLoad() {
        try {
            this.CLIENT_FILE = new File(CLIENT_CONFIG);
            this.initializedConfig.add(this.CLIENT_FILE);
            this.FRIEND_FILE = new File(FRIEND_CONFIG);
            this.initializedConfig.add(this.FRIEND_FILE);
            this.GUI_FILE = new File(GUI_CONFIG);
            this.initializedConfig.add(this.GUI_FILE);
            this.HUD_FILE = new File(HUD_CONFIG);
            this.initializedConfig.add(this.HUD_FILE);
            this.ALT_FILE = new File(ALT_CONFIG);
            this.initializedConfig.add(this.ALT_FILE);
        } catch (Exception e) {
            Kura.logger.error("Config files aren't exist or are broken!");
            return false;
        }
        return true;
    }

    private void checkFile(File file) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPath(File path) {
        try {
            if (!path.exists()) {
                path.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trySet(IModule mods, JsonObject jsonMod) {
        try {
            for (Setting value : mods.getSettingList()) {
                this.tryValue(mods.name, value, jsonMod);
            }
        } catch (Exception e) {
            Kura.logger.error("Cant set value for " + (mods.isHUD ? "HUD " : " module ") + mods.getName() + "!");
        }
    }

    private void tryValue(String name, Setting<?> setting, JsonObject jsonMod) {
        try {
            if (setting instanceof StringSetting) {
                String sValue = jsonMod.get(setting.getName()).getAsString();
                ((StringSetting) setting).setValue(sValue);
            }
            if (setting instanceof ColorSetting) {
                int rgba = jsonMod.get(setting.getName()).getAsInt();
                ((ColorSetting) setting).setValue(new Color(rgba, true));
            }
            if (setting instanceof BooleanSetting) {
                boolean bValue = jsonMod.get(setting.getName()).getAsBoolean();
                ((BooleanSetting) setting).setValue(bValue);
            }
            if (setting instanceof DoubleSetting) {
                double dValue = jsonMod.get(setting.getName()).getAsDouble();
                ((DoubleSetting) setting).setValue(dValue);
            }
            if (setting instanceof IntegerSetting) {
                int iValue = jsonMod.get(setting.getName()).getAsInt();
                ((IntegerSetting) setting).setValue(iValue);
            }
            if (setting instanceof FloatSetting) {
                float fValue = jsonMod.get(setting.getName()).getAsFloat();
                ((FloatSetting) setting).setValue(fValue);
            }
            if (setting instanceof ModeSetting) {
                ModeSetting modeValue = (ModeSetting) setting;
                modeValue.setValueByString(jsonMod.get(modeValue.getName()).getAsString());
            }
        } catch (Exception e) {
            Kura.logger.error("Cant set value for " + name + ",loaded default! Value name: " + setting.getName());
        }
    }

    private void trySetClient(JsonObject json) {
        try {
            Kura.commandPrefix.setValue(json.get("CommandPrefix").getAsString());
            BindCommand.modifiersEnabled.setValue(json.get(BindCommand.modifiersEnabled.getName()).getAsBoolean());
        } catch (Exception e) {
            Kura.logger.error("Error while setting Client!");
        }
    }

    static {
        gsonPretty = new GsonBuilder().setPrettyPrinting().create();
        jsonParser = new JsonParser();
    }
}

