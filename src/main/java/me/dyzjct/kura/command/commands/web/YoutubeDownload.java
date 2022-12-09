/*
package com.unknown.xg42.command.commands.web;

import com.google.api.services.youtube.model.SearchResult;
import com.unknown.xg42.command.Command;
import com.unknown.xg42.command.syntax.ChunkBuilder;
import com.unknown.xg42.music.API.YoutubeSearch;
import com.unknown.xg42.music.API.YoutubeConveter;
import com.unknown.xg42.utils.mc.ChatUtil;

import java.util.Objects;

public class YoutubeDownload
extends Command {
    public YoutubeDownload() {
        super("ytd", new ChunkBuilder().append("url/search").build());
        this.description = "download music on youtube (you your wifi pls && better use a vpn)";
    }

    @Override
    public void call(final String[] args) {
        if (args.length <= 1) {
            ChatUtil.NoSpam.sendWarnMessage("Pls Enter Url Or Search title");
        } else {
            new Thread("Youtube Music Download"){
                @Override
                public void run() {
                    try {
                        if (args[0] != null && args[0].contains("https://www.youtube.com") || Objects.requireNonNull(args[0]).contains("http://www.youtube.com")) {
                            ChatUtil.NoSpam.sendWarnMessage("Download Pls Wait...");
                            YoutubeConveter.youtubeToMP3(args[2], "XG42/music/");
                            ChatUtil.NoSpam.sendWarnMessage("Download Success!");
                        } else {
                            StringBuilder toSearch = new StringBuilder();
                            for (int i = 0; i < args.length - 1; ++i) {
                                toSearch.append(args[i]).append(" ");
                            }
                            ChatUtil.NoSpam.sendWarnMessage("Searching: " + toSearch +"...");
                            SearchResult result = YoutubeSearch.searchFirstOne(toSearch.toString());
                            if (result != null) {
                                ChatUtil.sendMessage("Find :" + result.getSnippet().getTitle());
                                ChatUtil.sendMessage("https://youtu.be/"+result.getId().getVideoId());
                                ChatUtil.NoSpam.sendMessage("Download...");
                                YoutubeConveter.youtubeToMP3("https://youtu.be/"+result.getId().getVideoId(), "XG42/music/", result.getSnippet().getTitle());
                                ChatUtil.NoSpam.sendWarnMessage("Download Success!");
                            }
                        }
                    }
                    catch (Exception e) {
                        ChatUtil.NoSpam.sendErrorMessage("Catch Error: " + e.getCause());
                        ChatUtil.sendErrorMessage("Pls give your log to dev for fix");
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}


 */
