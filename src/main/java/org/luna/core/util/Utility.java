package org.luna.core.util;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// All of my file handling,
public class Utility {
    private static ImageUtility imageUtility = new ImageUtility();
    private static SecureRandom rnd = new SecureRandom();

    private static Font smallFont = new Font("Consolas", Font.PLAIN, 8);
    // two sprite sheet helpers, one where you give path and one where you give image
    // sprite sheet maker helpers
    // load up an image

    public BufferedImage[] makeSpriteSheet(String path, int width, int height, int rows, int cols){
        BufferedImage baseSheet = imageUtility.load(path);
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
    //

    public static void deleteFolder(String dir){
        File directory = new File(dir);
        if(directory.exists()){
            try{
                delete(directory);
                System.out.println("Directory removed: " + dir);
            }catch (IOException e){
                System.out.println("error deleting folder");
                System.out.println(e.getMessage());
            }
        }
    }

    private static void delete(File file)
            throws IOException {

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(Objects.requireNonNull(file.list()).length==0){

                file.delete();
                //System.out.println("Directory is deleted : " + file.getAbsolutePath());

            }else{

                //list all the directory contents
                String[] files = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if(Objects.requireNonNull(file.list()).length==0){
                    file.delete();
                    //System.out.println("Directory is deleted : " + file.getAbsolutePath());
                }
            }

        }else{
            //if file, then delete it
            file.delete();
            //System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }//

    // print an array using generic input
    public static < T > void printArr(T[] arr){
        System.out.print("[");
        for(T var : arr){
            System.out.print(var + " ");
        }
        System.out.print("]\n");
    }// end of generic print arr

    public static < T > String makeArrString(T[] arr){
        StringBuilder out = new StringBuilder("[");
        for(T var : arr){
            out.append(var).append(" ");
        }
        out = new StringBuilder(out.substring(0, out.length() - 1));
        out.append("]");
        return out.toString();
    }

    public static String makeArrString(int[] arr){
        StringBuilder out = new StringBuilder("[");
        for(int var : arr){
            out.append(var).append(" ");
        }
        out = new StringBuilder(out.substring(0, out.length() - 1));
        out.append("]");
        return out.toString();
    }

    // return the most occurring object in an array
    public static < T > T getMode(T[] arr){
        Map<T, Integer> results = new HashMap<>();
        for(T obj : arr){
            if(results.containsKey(obj)){
                results.put(obj, results.get(obj)+1);
            }else{
                results.put(obj, 1);
            }
        }

        int mode = -1;
        T modeObj = arr[0];
        for(T key : results.keySet()){
            if(results.get(key) > mode){
                mode = results.get(key);
                modeObj = key;
            }
        }
        return modeObj;
    }

}
