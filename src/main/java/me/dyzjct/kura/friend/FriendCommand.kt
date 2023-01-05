package me.dyzjct.kura.friend

import me.dyzjct.kura.command.Command
import me.dyzjct.kura.command.syntax.ChunkBuilder
import me.dyzjct.kura.command.syntax.parsers.EnumParser
import me.dyzjct.kura.utils.mc.ChatUtil
import me.dyzjct.kura.utils.other.Friend

class FriendCommand :
    Command("friend", ChunkBuilder().append("mode", true, EnumParser("add", "del")).append("name").build(), "f") {
    init {
        setDescription("Add someone as your friend!")
    }

    override fun call(args2: Array<String>) {
        if (args2[0] == null) {
            if (FriendManager.INSTANCE.friends.isEmpty()) {
                ChatUtil.NoSpam.sendWarnMessage("You currently don't have any friends added. friend add <name> to add one.")
                return
            }
            var f = ""
            for (friend in FriendManager.INSTANCE.friends) {
                f = f + friend.name + ", "
            }
            f = f.substring(0, f.length - 2)
            ChatUtil.sendMessage("Your friends: $f")
            return
        }
        if (args2[1] == null) {
            ChatUtil.NoSpam.sendMessage(
                String.format(
                    if (FriendManager.isFriend(args2[0])) "Yes, %s is your friend." else "No, %s isn't a friend of yours.",
                    args2[0]
                )
            )
            return
        }
        if (args2[0].equals("add", ignoreCase = true) || args2[0].equals("new", ignoreCase = true)) {
            if (FriendManager.isFriend(args2[1])) {
                ChatUtil.NoSpam.sendWarnMessage("That player is already your friend.")
                return
            }
            Thread(Runnable {
                val f = Friend(args2[1], true)
                if (f == null) {
                    ChatUtil.NoSpam.sendErrorMessage("Failed to find UUID of " + args2[1])
                    return@Runnable
                }
                FriendManager.INSTANCE.friends.add(f)
                ChatUtil.NoSpam.sendMessage(ChatUtil.SECTIONSIGN.toString() + "b " + f.name + " has been friended.")
            }).start()
            return
        }
        if (args2[0].equals("del", ignoreCase = true) || args2[0].equals(
                "remove",
                ignoreCase = true
            ) || args2[0].equals("delete", ignoreCase = true)
        ) {
            if (!FriendManager.isFriend(args2[1])) {
                ChatUtil.NoSpam.sendWarnMessage("That player isn't your friend.")
                return
            }
            val friend = FriendManager.INSTANCE.friends.stream()
                .filter { friend1: Friend -> friend1.name.equals(args2[1], ignoreCase = true) }
                .findFirst().get()
            FriendManager.INSTANCE.friends.remove(friend)
            ChatUtil.NoSpam.sendWarnMessage(ChatUtil.SECTIONSIGN.toString() + "b " + friend.name + " has been unfriended.")
            return
        }
        ChatUtil.NoSpam.sendWarnMessage("Please specify either add or remove")
    }
}