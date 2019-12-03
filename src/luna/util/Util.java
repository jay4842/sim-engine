package luna.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Util {

    public static Random rnd = new Random();

    public static double random( double num ){//random method may not be the best
        return (num * 2)  * rnd.nextDouble() - num;
    }

    public static double randomPlus( double num ){//return only a positive number
        double temp = ((num * 2)  * rnd.nextDouble()) - num;
        if( temp < 0 )
            return temp * -1;
        else
            return temp;
    }

    public static int random(int num){ return rnd.nextInt(num); }

    // place particle helpers here maybe

    // Still working out some stuff but it works!
    public static Particle makeParticle(double x, double y, Color c, int scale){
        double dx = random(5);
        double dy = random(5);
        int size = (int)randomPlus(15) + 10;
        int life = (int)random(10);
        Particle  p = new Particle(x,y, dx, dy, size, life,c, scale);
        //p.setAcc(random(10),random(10));
        //p.setGrowth(random(10),random(10));
        return p;
    }

    // load up an image
    public static BufferedImage loadImage(String path){
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File(path));
        }catch (IOException ex){
            System.out.println("Error reading image");
        }catch (Exception ex){
            System.out.println("Unknown error : " + ex.getMessage());
        }
        // end
        return img;
    }
    // two sprite sheet helpers, one where you give path and one where you give image
    // sprite sheet maker helpers
    public static BufferedImage[] makeSpriteSheet(String path, int width, int height, int rows, int cols){
        BufferedImage baseSheet = loadImage(path);
        //System.out.println(baseSheet.getWidth());
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
    public static BufferedImage[] makeSpriteSheet(BufferedImage baseSheet, int width, int height, int rows, int cols){
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
    //

    // print an array using generic input
    public static < T > void printArr(T[] arr){
        System.out.print("[");
        for(T var : arr){
            System.out.print(var + " ");
        }
        System.out.print("]\n");
    }// end of generic print arr

    public void println(String s){
        System.out.println(s);
    }

    // other universal objects that should not be made for every entity.
    // map from string direction to actual direction values
    public static Map<String, Integer> stringToIntDirectionMap;
    public static Map<Integer, String> intToStringDirectionMap;

    // TODO: Consolidate all animations into one sprite library so we don't create a new animation for similar objects.
    // init anything here
    public Util(){
        stringToIntDirectionMap = new HashMap<>();
        intToStringDirectionMap = new HashMap<>();
        // set these guys up
        stringToIntDirectionMap.put("Left", 0);
        stringToIntDirectionMap.put("Right", 1);
        stringToIntDirectionMap.put("Up", 2);
        stringToIntDirectionMap.put("Down", 3);
        //
        stringToIntDirectionMap.put("left", 0);
        stringToIntDirectionMap.put("right", 1);
        stringToIntDirectionMap.put("up", 2);
        stringToIntDirectionMap.put("down", 3);
        // and vise versa
        intToStringDirectionMap.put(0,"Left");
        intToStringDirectionMap.put(1,"Right");
        intToStringDirectionMap.put(2,"Up");
        intToStringDirectionMap.put(3,"Down");
    }
}
