package luna.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
        try {
            writer = new PrintWriter(filename);
        } catch (FileNotFoundException e) {
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
