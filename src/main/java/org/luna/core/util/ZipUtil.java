package org.luna.core.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// Help from: https://mkyong.com/java/how-to-compress-files-in-zip-format/
public class ZipUtil {
    private List<String> fileList;

    public ZipUtil(){
        fileList = new ArrayList<>();
    }

    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    private void zipIt(String zipFile, String sourceFolder) throws IOException{

        byte[] buffer = new byte[1024];



        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        System.out.println("Output to Zip : " + zipFile);

        for(String file : this.fileList){

            System.out.println("File Added : " + file);
            ZipEntry ze= new ZipEntry(file);
            zos.putNextEntry(ze);

            FileInputStream in =
                    new FileInputStream(sourceFolder + File.separator + file);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
        }

        zos.closeEntry();
        //remember close it
        zos.close();

        System.out.println("Done");

    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     * @param node file or directory
     */
    private void generateFileList(File node, String sourceFolder){

        //add file only
        if(node.isFile()){
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString(), sourceFolder));
        }

        if(node.isDirectory()){
            String[] subNote = node.list();
            assert subNote != null;
            for(String filename : subNote){
                generateFileList(new File(node, filename), sourceFolder);
            }
        }

    }

    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file, String sourceFolder){
        return file.substring(sourceFolder.length()+1);
    }

    public boolean createZipFile(String folder, String zipName){
        generateFileList(new File(folder), folder);
        try{
            zipIt(zipName, folder);
        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
