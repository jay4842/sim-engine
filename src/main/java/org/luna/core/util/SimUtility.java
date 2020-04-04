package org.luna.core.util;

import java.awt.Font;
import java.io.*;
import com.jcraft.jsch.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
// All of my file handling,
public class SimUtility {

    private static SecureRandom rnd = new SecureRandom();
    private static HashMap<String, HashMap<String, String>> ftpConfig = new HashMap<>();
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static Font smallFont = new Font("Consolas", Font.PLAIN, 10);

    private static boolean loadFtpConfig(){
        String file = "res/ftpConfig.json";
        Object obj = null;

        try {
            obj = new JSONParser().parse(new FileReader(file));
            JSONObject jo = (JSONObject) obj;
            JSONArray conf = (JSONArray) jo.get("config");
            System.out.println(conf.toString());
            Iterator iterator = conf.iterator();

            while (iterator.hasNext()) {
                JSONObject piConf = (JSONObject) iterator.next();
                System.out.println(piConf.toString());
                HashMap<String, String> targetMap = new HashMap<>();
                // now set the map info
                targetMap.put("name", (String) piConf.get("name"));
                targetMap.put("ip", (String) piConf.get("ip"));
                targetMap.put("destFolder", (String) piConf.get("destFolder"));
                targetMap.put("pass", (String) piConf.get("pass"));
                targetMap.put("user", (String) piConf.get("user"));
                ftpConfig.put(targetMap.get("name"), targetMap);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ftpConfig.keySet().size() > 0;
    }

    public static void deleteFolder(String dir){
        File directory = new File(dir);
        if(directory.exists()){
            delete(directory);
            System.out.println("Directory removed: " + dir);
        }
    }

    private static void deleteOutput(boolean x){
        if(x)
            System.out.println("file deleted!");
        else
            System.out.println("error deleting file!");
    }

    private static void delete(File file) {

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(Objects.requireNonNull(file.list()).length==0)
                deleteOutput(file.delete());
            else{

                //list all the directory contents
                String[] files = file.list();

                assert files != null;
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if(Objects.requireNonNull(file.list()).length==0){
                    deleteOutput(file.delete());
                    //System.out.println("Directory is deleted : " + file.getAbsolutePath());
                }
            }

        }else{
            //if file, then delete it
            deleteOutput(file.delete());
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
        if(out.length() > 0) out.append("]");
        return out.toString();
    }

    public static String makeArrString(int[] arr){
        StringBuilder out = new StringBuilder("[");
        for(int var : arr){
            out.append(var).append(" ");
        }
        out = new StringBuilder(out.substring(0, out.length() - 1));
        if(out.length() > 0) out.append("]");
        return out.toString();
    }

    public static String makeArrString(short[] arr){
        StringBuilder out = new StringBuilder("[");
        for(short var : arr){
            out.append(var).append(" ");
        }
        out = new StringBuilder(out.substring(0, out.length() - 1));
        if(out.length() > 0) out.append("]");
        return out.toString();
    }

    public static String makeArrString(float[] arr){
        StringBuilder out = new StringBuilder("[");
        for(float var : arr){
            out.append(var).append(" ");
        }
        out = new StringBuilder(out.substring(0, out.length() - 1));
        if(out.length() > 0) out.append("]");
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

    public static float getAverage(Object[] list){
        int x = 0;
        for(Object amt : list)
            x += (int)amt;

        return (float) x / (float)list.length;
    }

    public static SecureRandom getRnd() {
        return rnd;
    }

    public static Font getSmallFont() {
        return smallFont;
    }


    // TODO: add option based on OS
    // The user drop can only access the dropoff folder
    private static ChannelSftp setupJsch() throws JSchException, FileNotFoundException {

        if(isMac() || isUnix()) {
            JSch jsch = new JSch();
            jsch.setKnownHosts(new FileInputStream("/Users/jelly_kid/.ssh/known_hosts"));
            Session jschSession = jsch.getSession("drop", "192.168.0.18");
            jschSession.setPassword("drop&1");
            jschSession.connect();
            return (ChannelSftp) jschSession.openChannel("sftp");
        }
            return new ChannelSftp();
    }

    // connect to my PI, and send the file over sftp
    // - return true if success
    // - return false if failed
    public static boolean sendFileOverSftp(String filename){
        if(ftpConfig.keySet().size() < 1) {
            if(!loadFtpConfig()){
                System.out.println("FTP config not loaded!");
                return false;
            }
        }

        try{
            String dest = "/";
            ChannelSftp channelSftp = setupJsch();
            channelSftp.connect();
            channelSftp.put(filename, dest);
            channelSftp.exit();
            return true;

        }catch (JSchException | SftpException | FileNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }

    // TODO : add way to check what system it is on and build paths based on that
    public static boolean sendFolderOverSftp(String folderName){
        // create a tmp zip file of the folder
        // then send the zip
        ZipUtil zip = new ZipUtil();
        String root = "/Users/jelly_kid/IdeaProjects/sim-engine/";
        boolean zipped = zip.createZipFile(folderName, root + "tmp/tmp.zip");
        if(zipped){
            boolean sent = sendFileOverSftp(root + "tmp/tmp.zip");

            delete(new File(root + "tmp/tmp.zip"));
            // remove the tmp zip
            return sent;
        }else
            return false;
    }

    // https://stackoverflow.com/questions/8911356/whats-the-best-practice-to-round-a-float-to-2-decimals
    public static float round(float d) {
        return Math.round(d * 100.0f) / 100.0f;
    }

    public static <type> JSONArray arrayToJSONArray(type[] array){
        JSONArray out = new JSONArray();
        for(type t : array)
            out.add(t);
        return out;
    }

    public static JSONArray arrayToJSONArray(int[] array){
        JSONArray out = new JSONArray();
        for(int t : array)
            out.add(t);
        return out;
    }

    public static JSONArray arrayToJSONArray(float[] array){
        JSONArray out = new JSONArray();
        for(float f : array)
            out.add(f);
        return out;
    }

    public static JSONArray arrayToJSONArray(short[] array){
        JSONArray out = new JSONArray();
        for(short s : array)
            out.add(s);
        return out;
    }

    // os checks: https://mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname
    public static boolean isWindows() {

        return (OS.contains("win"));

    }

    public static boolean isMac() {

        return (OS.contains("mac"));

    }

    public static boolean isUnix() {

        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 );

    }

    public static boolean isSolaris() {

        return (OS.contains("sunos"));

    }

}
