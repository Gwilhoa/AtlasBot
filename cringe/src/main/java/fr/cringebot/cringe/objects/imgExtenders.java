/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   imgExtenders.java                                  :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:07:13 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/05 13:37:15 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.objects;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * tout ce qui touche au images
 */
public class imgExtenders {
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
    public static BufferedImage getImage(URL url){
        BufferedImage image;
        try {
            image = ImageIO.read(url);
        } catch (Exception e) {
            image = null;
            e.printStackTrace();
        }
        return image;
    }
}
