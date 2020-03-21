package org.luna.core.reporting;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class Report {

    //private Map<String, Object> reportData; // TODO: see if I need to use this or remove it
    private String reportFile;
    private PrintWriter writer;
    private Timestamp timestamp;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public Report(String filename){
        this.reportFile = filename;
        timestamp = new Timestamp(System.currentTimeMillis());
        openPrintWriter();
    }

    // open file, create path if needed
    public void openPrintWriter(){
        String [] dir = reportFile.split("/");
        StringBuilder dirPath = new StringBuilder();
        for(int i = 0; i < dir.length-1; i++){
            if(i < dir.length-2)
                dirPath.append(dir[i]).append("/");
            else
                dirPath.append(dir[i]);
        }
        try {
            Files.createDirectories(Paths.get(dirPath.toString()));
            writer = new PrintWriter(reportFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//

    // some simple write functions
    public void writeLn(String line, int step){
        timestamp.setTime(System.currentTimeMillis());
        this.writer.print(timestamp.toString() + "|" + step + "|" + line + "\n");
        this.writer.flush();
    }//

    public void write(String line){
        this.writer.print(line);
        this.writer.flush();
    }//

    public void write(List<String> lines){
        for(String line : lines){
            timestamp.setTime(System.currentTimeMillis());
            this.writer.print("[" + timestamp.toString() + "] " + line + "\n");
            this.writer.flush();
        }
    }//

    public void writeLnNoTimestamp(String line, int step){
        this.writer.print(step + "|" + line + "\n");
        this.writer.flush();
    }

    public void writeTimestamp(String line, int step){
        timestamp.setTime(System.currentTimeMillis());
        this.writer.print(timestamp.toString() + "|" + step + "|" + line);
        this.writer.flush();
    }

    public void closeReport(){
        try{
            writer.close();
        }catch (Exception ex){
            System.out.println("Error closing report " + reportFile + ": " + ex.getMessage());
        }
    }

}
