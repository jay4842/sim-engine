package luna.util;

import luna.main.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

// for creating a log of current status
// will be used much later
public class Logger {

    // base logger
    private PrintWriter writer;
    private Timestamp timestamp;
    private String filename;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    public Logger(String filename){
        this.filename = filename;
        // open the file
        String [] dir = filename.split("/");
        String dirPath = "";
        for(int i = 0; i < dir.length-1; i++){
            if(i < dir.length-2)
                dirPath += dir[i] + "/";
            else
                dirPath += dir[i];
        }
        //System.out.println(dirPath);

        try {
            Files.createDirectories(Paths.get(dirPath));
            writer = new PrintWriter(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        timestamp = new Timestamp(System.currentTimeMillis());
    }// end of that

    // some simple write functions\
    public void write(String line){
        timestamp.setTime(System.currentTimeMillis());
        this.writer.print("[" + timestamp.toString() + "] (" + Game.iterationCount + ") " + line + "\n");
        this.writer.flush();
    }//

    public void write(List<String> lines){
        for(String line : lines){
            timestamp.setTime(System.currentTimeMillis());
            this.writer.print("[" + timestamp.toString() + "] " + line + "\n");
            this.writer.flush();
        }
    }//

    public void writeNoTimestamp(String line){
        timestamp.setTime(System.currentTimeMillis());
        this.writer.print(line + "\n");
        this.writer.flush();
    }

    public void copyLog(String destFile){
        // make our directory if needed
        String [] dir = destFile.split("/");
        String dirPath = "";
        for(int i = 0; i < dir.length-1; i++){
            if(i < dir.length-2)
                dirPath += dir[i] + "/";
            else
                dirPath += dir[i];
        }

        try{
            Files.createDirectories(Paths.get(dirPath));
            Files.copy(Paths.get(filename), Paths.get(destFile),
                    StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void closeWriter(){
        this.writer.close();
    }
}
