package org.luna.core.reporting;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class JSONReport {
    private String reportFile;
    private FileWriter writer;
    private Timestamp timestamp;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public JSONReport(String filename){
        this.reportFile = filename;
        timestamp = new Timestamp(System.currentTimeMillis());
        openFileWriter();
    }

    // open file, create path if needed
    private void openFileWriter(){
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
            writer = new FileWriter(reportFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//
}
