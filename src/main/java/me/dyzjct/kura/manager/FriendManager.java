package me.dyzjct.kura.manager;

import me.dyzjct.kura.utils.other.Friend;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class FriendManager {
    public static FriendManager INSTANCE;
    public ArrayList<Friend> friends = new ArrayList<>();

    public FriendManager() {
        INSTANCE = this;
    }

    public static void addFriend(String name) {
        if (FriendManager.isNull()) {
            return;
        }
        if (!INSTANCE.checkExist(name)) {
            FriendManager.INSTANCE.friends.add(new Friend(name, true));
        } else {
            FriendManager.INSTANCE.getFriendByName(name).isFriend = true;
        }
    }

    public static void removeFriend(String name) {
        if (FriendManager.isNull()) {
            return;
        }
        if (INSTANCE.checkExist(name)) {
            FriendManager.INSTANCE.getFriendByName(name).isFriend = false;
        }
    }

    public static boolean isNull() {
        return INSTANCE == null;
    }

    public static boolean isFriend(String name) {
        if (FriendManager.isNull()) {
            return false;
        }
        if (!INSTANCE.checkExist(name)) {
            return false;
        }
        return FriendManager.INSTANCE.getFriendByName(name).isFriend;
    }

    public static boolean isFriend(Entity entity) {
        return FriendManager.isFriend(entity.getName());
    }

    public static List<Friend> getFriendList() {
        if (FriendManager.isNull()) {
            return new ArrayList<Friend>();
        }
        return FriendManager.INSTANCE.friends;
    }

    public static List<String> getFriendStringList() {
        if (FriendManager.isNull()) {
            return new ArrayList<String>();
        }
        ArrayList<String> stringList = new ArrayList<>();
        FriendManager.getFriendList().forEach(f -> stringList.add(f.name));
        return stringList;
    }

    public boolean checkExist(String name) {
        for (Friend friend : this.friends) {
            if (!friend.name.equalsIgnoreCase(name)) continue;
            return true;
        }
        return false;
    }

    public Friend getFriendByName(String name) {
        for (Friend friend : this.friends) {
            if (!friend.name.equalsIgnoreCase(name)) continue;
            return friend;
        }
        return null;
    }
}

