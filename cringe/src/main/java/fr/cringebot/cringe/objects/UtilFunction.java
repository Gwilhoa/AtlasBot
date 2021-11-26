package fr.cringebot.cringe.objects;

import net.dv8tion.jda.api.entities.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UtilFunction {
    private static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    private static final Pattern p = Pattern.compile(URL_REGEX);

    public static boolean percent(int i){
        int r = new Random().nextInt(100);
        return r < i;
    }

    public static boolean isImage(String message) {
        Matcher m = p.matcher(message);
        if (m.find()) {
            String url = m.group();
            try {
                URL u = new URL(url);
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:77.0) Gecko/20100101 Firefox/77.0");
                try (InputStream input = c.getInputStream()) {
                    try {
                        ImageIO.read(input).toString();
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static boolean isVideo(String message) {
        Matcher m = p.matcher(message);
        if (m.find()) {
            String url = m.group();
            try {
                URL u = new URL(url);
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:77.0) Gecko/20100101 Firefox/77.0");
                try (InputStream input = c.getInputStream()) {
                    try {
                        byte[] buf = new byte[4 + 8];
                        int i = input.read(buf);
                        if (i < 6) {
                            //too short
                            return false;
                        }
                        byte[] buf6 = Arrays.copyOf(buf, 6);
                        if (Arrays.equals(buf6, new byte[]{0x47, 0x49, 0x46, 0x38, 0x37, 0x61})) {
                            return true;
                        }
                        if (Arrays.equals(buf6, new byte[]{0x47, 0x49, 0x46, 0x38, 0x39, 0x61})) {
                            return true;
                        }
                        if (i < 4 + 8) {
                            //too short
                            return false;
                        }
                        byte[] buf4 = Arrays.copyOfRange(buf, 4, buf.length);
                        if (Arrays.equals(buf4, new byte[]{0x66, 0x74, 0x79, 0x70, 0x69, 0x73, 0x6F, 0x6D})) {
                            return true;
                        }
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static boolean isTwitter(String message) {
        Matcher m = p.matcher(message);
        if (m.find()) {
            String url = m.group();
            return url.contains("twitter.com");
        }
        return false;
    }

    public static boolean isYoutube(String message) {
        Matcher m = p.matcher(message);
        if (m.find()) {
            String url = m.group();
            return url.contains("www.youtube.com") || url.contains("youtu.be"); //short links
        }
        return false;
    }

    public static boolean isAnyLink(Message msg)
    {
        //image link
        return UtilFunction.isTwitter(msg.getContentRaw()) //twitter.com link
                || UtilFunction.isYoutube(msg.getContentRaw()) //www.youtube.com & youtu.be link
                || (msg.getAttachments().size() >= 1 && msg.getAttachments().get(0).isImage()) //image attachment
                || (msg.getAttachments().size() >= 1 && msg.getAttachments().get(0).isVideo())    //video attachment
                || (msg.getAttachments().size() >= 1 && msg.getAttachments().get(0).getHeight() != -1) // image/video added test
                || UtilFunction.isVideo(msg.getContentRaw()) //video link
                || UtilFunction.isImage(msg.getContentRaw());
    }
}
