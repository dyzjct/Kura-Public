package me.dyzjct.kura.utils.player;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PlayerUtils{
    private static final JsonParser PARSER = new JsonParser();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static String getNameFromUUID(UUID uuid) {
        try {
            lookUpName process = new lookUpName(uuid);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getName();
        } catch (Exception e) {
            return null;
        }
    }
    public static EntityPlayer getNearestPlayer(final double range) {
        return (EntityPlayer)PlayerUtils.mc.world.playerEntities.stream().filter(p -> PlayerUtils.mc.player.getDistance(p) <= range).filter(p -> PlayerUtils.mc.player.getEntityId() != p.getEntityId()).min(Comparator.comparing(p -> PlayerUtils.mc.player.getDistance(p))).orElse(null);
    }

    public static EntityPlayer getLookingPlayer(final double range) {
        final List<EntityPlayer> players = new ArrayList<EntityPlayer>(PlayerUtils.mc.world.playerEntities);
        for (int i = 0; i < players.size(); ++i) {
            if (getDistance((Entity)players.get(i)) > range) {
                players.remove(i);
            }
        }
        players.remove(PlayerUtils.mc.player);
        EntityPlayer target = null;
        final Vec3d positionEyes = PlayerUtils.mc.player.getPositionEyes(PlayerUtils.mc.getRenderPartialTicks());
        final Vec3d rotationEyes = PlayerUtils.mc.player.getLook(PlayerUtils.mc.getRenderPartialTicks());
        final int precision = 2;
        for (int j = 0; j < (int)range; ++j) {
            for (int k = precision; k > 0; --k) {
                for (final EntityPlayer targetTemp : players) {
                    final AxisAlignedBB playerBox = targetTemp.getEntityBoundingBox();
                    final double xArray = positionEyes.x + rotationEyes.x * j + rotationEyes.x / k;
                    final double yArray = positionEyes.y + rotationEyes.y * j + rotationEyes.y / k;
                    final double zArray = positionEyes.z + rotationEyes.z * j + rotationEyes.z / k;
                    if (playerBox.maxY >= yArray && playerBox.minY <= yArray && playerBox.maxX >= xArray && playerBox.minX <= xArray && playerBox.maxZ >= zArray && playerBox.minZ <= zArray) {
                        target = targetTemp;
                    }
                }
            }
        }
        return target;
    }
    public static BlockPos getPlayerPos() {
        return new BlockPos(PlayerUtils.mc.player.posX, PlayerUtils.mc.player.posY, PlayerUtils.mc.player.posZ);
    }

    public static BlockPos getPlayerPos(final EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static double getDistance(final Entity entity) {
        return PlayerUtils.mc.player.getDistance(entity);
    }

    public static double getDistance(final BlockPos pos) {
        return PlayerUtils.mc.player.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
    }

    public static double getDistanceI(final BlockPos pos) {
        return getPlayerPos((EntityPlayer)PlayerUtils.mc.player).getDistance(pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockPos min(final List<BlockPos> posList) {
        return posList.stream().min(Comparator.comparing(b -> getDistance(b))).orElse(null);
    }

    public static String getNameFromUUID(String uuid) {
        try {
            lookUpName process = new lookUpName(uuid);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getName();
        } catch (Exception e) {
            return null;
        }
    }

    public static UUID getUUIDFromName(String name) {
        try {
            lookUpUUID process = new lookUpUUID(name);
            Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getUUID();
        } catch (Exception e) {
            return null;
        }
    }

    public static String requestIDs(String data) {
        try {
            String query = "https://api.mojang.com/profiles/minecraft";
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String res = convertStreamToString(in);
            in.close();
            conn.disconnect();
            return res;
        } catch (Exception e) {
            return null;
        }
    }
    public static BlockPos getPlayerPosFloored(final Entity p_Player) {
        return new BlockPos(Math.floor(p_Player.posX), Math.floor(p_Player.posY), Math.floor(p_Player.posZ));
    }

    public static BlockPos getPlayerPosFloored(final Vec3d pos, final double h) {
        return new BlockPos(Math.floor(pos.x), Math.floor(pos.y + h), Math.floor(pos.z));
    }

    public static BlockPos getPlayerPosFloored(final double x, final double y, final double z) {
        return new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    public static boolean isInLiquid() {
        return mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() instanceof BlockLiquid;
    }

    public static BlockPos GetPlayerPosHighFloored(final Entity p_Player) {
        return getPlayerPosFloored(p_Player, 0.2);
    }

    public static BlockPos getPlayerPosFloored(final Entity p_Player, final double y) {
        return new BlockPos(Math.floor(p_Player.posX), Math.floor(p_Player.posY + y), Math.floor(p_Player.posZ));
    }
    public static String convertStreamToString(InputStream is) {
        Scanner s = (new Scanner(is)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "/";
    }

    public static List<String> getHistoryOfNames(UUID id) {
        try {
            JsonArray array = getResources(new URL("https://api.mojang.com/user/profiles/" + getIdNoHyphens(id) + "/names"), "GET").getAsJsonArray();
            List<String> temp = Lists.newArrayList();
            for (JsonElement e : array) {
                JsonObject node = e.getAsJsonObject();
                String name = node.get("name").getAsString();
                long changedAt = node.has("changedToAt") ? node.get("changedToAt").getAsLong() : 0L;
                temp.add(name + "Â§8" + (new Date(changedAt)).toString());
            }
            Collections.sort(temp);
            return temp;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String getIdNoHyphens(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    private static JsonElement getResources(URL url, String request) throws Exception {
        return getResources(url, request, null);
    }

    private static JsonElement getResources(URL url, String request, JsonElement element) throws Exception {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(request);
            connection.setRequestProperty("Content-Type", "application/json");
            if (element != null) {
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                output.writeBytes(AdvancementManager.GSON.toJson(element));
                output.close();
            }
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }
            scanner.close();
            String json = builder.toString();
            JsonElement data = PARSER.parse(json);
            return data;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public static class lookUpUUID implements Runnable {
        private final String name;
        private volatile UUID uuid;

        public lookUpUUID(String name) {
            this.name = name;
        }

        public void run() {
            NetworkPlayerInfo profile;
            try {
                ArrayList<NetworkPlayerInfo> infoMap = new ArrayList<>(Objects.requireNonNull(mc.getConnection()).getPlayerInfoMap());
                profile = infoMap.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
                assert profile != null;
                this.uuid = profile.getGameProfile().getId();
            } catch (Exception e) {
                profile = null;
            }
            if (profile == null) {
                ChatUtil.sendMessage("Player isn't online. Looking up UUID..");
                String s = PlayerUtils.requestIDs("[\"" + this.name + "\"]");
                if (s == null || s.isEmpty()) {
                    ChatUtil.sendMessage("Couldn't find player ID. Are you connected to the internet? (0)");
                } else {
                    JsonElement element = (new JsonParser()).parse(s);
                    if (element.getAsJsonArray().size() == 0) {
                        ChatUtil.sendMessage("Couldn't find player ID. (1)");
                    } else {
                        try {
                            String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                            this.uuid = UUIDTypeAdapter.fromString(id);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ChatUtil.sendMessage("Couldn't find player ID. (2)");
                        }
                    }
                }
            }
        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class lookUpName implements Runnable {
        private final String uuid;
        private final UUID uuidID;
        private volatile String name;

        public lookUpName(String input) {
            this.uuid = input;
            this.uuidID = UUID.fromString(input);
        }

        public lookUpName(UUID input) {
            this.uuidID = input;
            this.uuid = input.toString();
        }

        public void run() {
            this.name = lookUpName();
        }

        public String lookUpName() {
            EntityPlayer player = null;
            if (mc.world != null) {
                player = mc.world.getPlayerEntityByUUID(this.uuidID);
            }
            if (player == null) {
                final String url = "https://api.mojang.com/user/profiles/" + this.uuid.replace("-", "") + "/names";
                try {
                    final String nameJson = IOUtils.toString(new URL(url));
                    if (nameJson.contains(",")) {
                        List<String> names = Arrays.asList(nameJson.split(","));
                        Collections.reverse(names);
                        return names.get(1).replace("{\"name\":\"", "").replace("\"", "");
                    } else {
                        return nameJson.replace("[{\"name\":\"", "").replace("\"}]", "");
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                    return null;
                }
            }
            return player.getName();
        }

        public String getName() {
            return this.name;
        }
    }
}
