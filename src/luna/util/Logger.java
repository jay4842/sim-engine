package luna.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

// for creating a log of current status
// will be used much later
public class Logger {

    // base logger
    private PrintWriter writer;
    private Timestamp timestamp;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    public Logger(String filename){
        // open the file
        String [] dir = filename.split("/");
        String dirPath = "";
        for(int i = 0; i < dir.length-1; i++){
            if(i < dir.length-2)
                dirPath += dir[i] + "/";
            else
                dirPath += dir[i];
        }
        System.out.println(dirPath);

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
        timestamp.getTime();
        this.writer.write("[" + timestamp.toString() + "] " + line + "\n");
    }//

    public void write(List<String> lines){
        for(String line : lines){
            timestamp.getTime();
            this.writer.write("[" + timestamp.toString() + "] " + line + "\n");
        }
    }//

    public void writeNoTimestamp(String line){
        this.writer.write(line + "\n");
    }

    public void closeWriter(){
        this.writer.close();
    }
}
