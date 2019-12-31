package org.luna.core.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtility {

    public static BufferedImage load(String path){
        BufferedImage img = null;
        try {img = ImageIO.read(new File(path));} catch (IOException e) {
            System.out.println("Failed to load image pointing to " + path);
        }
        return img;
    }

    public static BufferedImage changeImageHue(int iHUE, BufferedImage img){
        float hue = iHUE/360.0f;

        BufferedImage raw,processed;
        int WIDTH = img.getWidth();
        int HEIGHT = img.getHeight();
        processed = new BufferedImage(WIDTH,HEIGHT,img.getType());

        for(int Y=0; Y<HEIGHT;Y++)
        {
            for(int X=0;X<WIDTH;X++)
            {
                int RGB = img.getRGB(X,Y);
                int R = (RGB >> 16) & 0xff;
                int G = (RGB >> 8) & 0xff;
                int B = (RGB) & 0xff;
                float HSV[]=new float[3];
                Color.RGBtoHSB(R,G,B,HSV);
                if((RGB>>24) != 0x00)
                    processed.setRGB(X,Y,Color.getHSBColor(hue,HSV[1],HSV[2]).getRGB());
            }
        }

        return processed;
    }
}
