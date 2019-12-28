package org.luna.core.util;

import javax.imageio.ImageIO;
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
}
