package luna.util;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {
	public static BufferedImage load(String path){
		BufferedImage img = null;
		try {img = ImageIO.read(new File(path));} catch (IOException e) {
			System.out.println("Failed to load image pointing to " + path);
		}
		return img;
	}
}
