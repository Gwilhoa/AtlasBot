package fr.cringebot.cringe.objects;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class imgExtenders {
    public static BufferedImage resize(BufferedImage img, int newW, int newH, int posx, int posy, boolean scale){
        return resize(img,newW,newH,posx,posy,scale,true);
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH, int posx, int poxy, boolean scale, boolean smooth) { //sert a reformer une image qu'on prends sur internet
        Image tmp;
        if (scale) {
            if(smooth) {
                tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            }else{
                tmp = img.getScaledInstance(newW, newH, Image.SCALE_REPLICATE);
            }
        } else {
            tmp = img;
        }
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, posx, poxy, null);
        g2d.dispose();

        return dimg;
    }

    public static BufferedImage getImage(String name){
        BufferedImage image;
        try {
            image = ImageIO.read(imgExtenders.class.getClassLoader().getResource(name));
        } catch (Exception e) {
            image = null;
            e.printStackTrace();
        }
        return image;
    }
    public static BufferedImage getImage(URL url){
        BufferedImage image;
        System.out.println("yes..");
        try {
            image = ImageIO.read(url);
        } catch (Exception e) {
            image = null;
            e.printStackTrace();
        }
        return image;
    }
}
