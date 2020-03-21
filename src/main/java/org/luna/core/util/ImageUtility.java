package org.luna.core.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageUtility {

    private static Utility util = new Utility();

    private static Map<String, BufferedImage[]> animationMap = new HashMap<>();
    private static Map<String, BufferedImage> tileImagesMap = new HashMap<>();

    public ImageUtility(){
        //
        if(animationMap.size() == 0) {
            String leftPath = "res/entity/Left_slime_bob.png";
            String rightPath = "res/entity/Right_slime_bob.png";
            String upPath = "res/entity/Up_slime_bob.png";
            String DownPath = "res/entity/Down_slime_bob.png";
            String talkingPath = "res/emote/speaking_sheet.png";
            // first lets make all the sheets for each animation
            animationMap.put("left_base", makeSpriteSheet(leftPath, 16, 16, 5, 1));
            animationMap.put("right_base", makeSpriteSheet(rightPath, 16, 16, 5, 1));
            animationMap.put("up_base", makeSpriteSheet(upPath, 16, 16, 5, 1));
            animationMap.put("down_base", makeSpriteSheet(DownPath, 16, 16, 5, 1));
            animationMap.put("talking", makeSpriteSheet(talkingPath, 8, 8, 4, 1));
            // alright now we can place these guys in the animation maps
            animationMap.put("left_MutationA", animationMap.get("left_base"));
            animationMap.put("right_MutationA", animationMap.get("right_base"));
            animationMap.put("up_MutationA", animationMap.get("up_base"));
            animationMap.put("down_MutationA", animationMap.get("down_base"));
            // now make other mutations

            animationMap.put("left_MutationB", adjustImageListHue("left_base", 50));
            animationMap.put("right_MutationB", adjustImageListHue("right_base", 50));
            animationMap.put("up_MutationB", adjustImageListHue("up_base", 50));
            animationMap.put("down_MutationB", adjustImageListHue("down_base", 50));
            //
            animationMap.put("left_MutationC", adjustImageListHue("left_base", 192));
            animationMap.put("right_MutationC", adjustImageListHue("right_base", 192));
            animationMap.put("up_MutationC", adjustImageListHue("up_base", 192));
            animationMap.put("down_MutationC", adjustImageListHue("down_base", 192));
            //
            animationMap.put("left_MutationD", adjustImageListHue("left_base", 277));
            animationMap.put("right_MutationD", adjustImageListHue("right_base", 277));
            animationMap.put("up_MutationD", adjustImageListHue("up_base", 277));
            animationMap.put("down_MutationD", adjustImageListHue("down_base", 277));
        }

        if(tileImagesMap.size() == 0) {
            // tile map
            String grassTilePath = "res/tile/grass_tile.png";
            String barrenTilePath = "res/tile/barren_tile.png";
            String duneTilePath = "res/tile/dune_tile.png";
            String mountainTilePath = "res/tile/mountain_tile.png";
            tileImagesMap.put("grass", load(grassTilePath));
            tileImagesMap.put("barren", load(barrenTilePath));
            tileImagesMap.put("dune", load(duneTilePath));
            tileImagesMap.put("mountain", load(mountainTilePath));
        }
    }

    // get a loaded list and create a new list of adjusted hue
    public BufferedImage[] adjustImageListHue(String key, int iHUE){
        BufferedImage[] adjustedList = new BufferedImage[animationMap.get(key).length];
        for(int i = 0; i < animationMap.get(key).length; i++){
            BufferedImage img = animationMap.get(key)[i];
            img = changeImageHue(iHUE, img);
            adjustedList[i] = img;

        }
        return adjustedList;
    }

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
                float[] HSV =new float[3];
                Color.RGBtoHSB(R,G,B,HSV);
                if((RGB>>24) != 0x00)
                    processed.setRGB(X,Y,Color.getHSBColor(hue,HSV[1],HSV[2]).getRGB());
            }
        }

        return processed;
    }

    public BufferedImage getSpriteImage(String type, int frame){
        if(animationMap.containsKey(type)){
            return animationMap.get(type)[frame];
        }
        return null;
    }

    public BufferedImage[] makeSpriteSheet(String path, int width, int height, int rows, int cols){
        BufferedImage baseSheet = load(path);
        //System.out.println(path + " " + baseSheet.getWidth());
        BufferedImage[] sprites = new BufferedImage[rows * cols];
        //
        for(int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                //System.out.println("Sprite at[" + (x * rows) + y + "] image Start " + x*width + " " + y*height);
                sprites[(x * rows) + y] = baseSheet.getSubimage(x*width,y*height,width,height);
            }
        }
        return sprites;
    }// end of this guy
    //
    public BufferedImage[] makeSpriteSheet(BufferedImage baseSheet, int width, int height, int rows, int cols){
        BufferedImage[] sprites = new BufferedImage[rows * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sprites[(i * cols) + j] = baseSheet.getSubimage(
                        j * width,
                        i * height,
                        width,
                        height
                );
            }
        }//
        return sprites;
    }// end of this guy

    public BufferedImage getTileImage(String type){
        if(tileImagesMap.containsKey(type))
            return tileImagesMap.get(type);
        return null;
    }

    public Color getMainImageColor(String sprite){
        if(animationMap.containsKey(sprite)) {
            BufferedImage img = animationMap.get(sprite)[0];
            int p = img.getRGB(img.getWidth()/2, img.getHeight()/2); // get pixel value at center of image
            //get alpha
            int a = (p>>24) & 0xff;
            //get red
            int r = (p>>16) & 0xff;
            //get green
            int g = (p>>8) & 0xff;
            //get blue
            int b = p & 0xff;
            return new Color(r,g,b,a);
        }
        return Color.gray;
    }
}
