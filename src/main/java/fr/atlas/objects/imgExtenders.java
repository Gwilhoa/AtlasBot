/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   imgExtenders.java                                  :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:07:13 by gchatain          #+#    #+#             */
/*   Updated: 2022/06/19 11:22:52 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.atlas.objects;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * tout ce qui touche au images
 */
public class imgExtenders {
    private static final Lock download = new ReentrantLock();
    //sert a re tailler une image


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

    /**
     * sert a récuperer une image integrer au bot
     * @param name
     * @return
     */
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

    /**
     * sert a récuperer une image depuis internet
     * @param url
     * @return
     */
    public static BufferedImage getImage(URL url) {
        download.lock();
        BufferedImage image;
        try {
            image = ImageIO.read(url);
        } catch (Exception e) {
            image = null;
            e.printStackTrace();
        }
        download.unlock();
        return image;
    }

    public static File getFile(String name) throws IOException {
        BufferedInputStream bs = new BufferedInputStream(imgExtenders.class.getClassLoader().getResource(name).openStream());
        downloadFile(name, bs);
        return new File(name);
    }

    public static File getFile(String url, String name, String directory) throws IOException {
        return getFile(new URL(url), name, directory);
    }
    
    public static File getFile(URL url, String name, String directory) throws IOException {
        BufferedInputStream bs = new BufferedInputStream(url.openStream());
        if (directory == null) {
            downloadFile(name, bs);
            return new File(name);
        }
        downloadFile(directory, bs);
        return new File(directory);
    }

    private static void downloadFile(String name, BufferedInputStream bs) throws IOException {
        download.lock();
        FileOutputStream fos = new FileOutputStream(name);
        byte[] data = new byte[1024];
        int ByteContent;
        while ((ByteContent = bs.read(data, 0, 1024)) != -1) {
            fos.write(data, 0, ByteContent);
        }
        bs.close();
        fos.close();
        download.unlock();
    }
    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }
}
