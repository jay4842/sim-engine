package luna.util;

import java.io.File;
import java.io.FileNotFoundException;
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
        // lets make sure the folders are setup correctly
        String [] split = filename.substring(1).split("/");
        String dir = ".";
        for(int i = 0; i < split.length-1; i++){
            dir += split[i];
            if(i < split.length-2)
                dir += "/";
        }
        //System.out.println(dir);

        // open the file
        try {
            Files.createDirectories(Paths.get(dir));
            writer = new PrintWriter(filename, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        timestamp = new Timestamp(System.currentTimeMillis());
    }// end of that

    // some simple write functions
    // TODO finish defining timestamp
    public void write(String line){
        this.writer.write(line);
    }//

    public void write(List<String> lines){
        for(String line : lines){
            timestamp.getTime();
            this.writer.write(line);
        }
    }//

    public void closeWriter(){
        this.writer.close();
    }
}
