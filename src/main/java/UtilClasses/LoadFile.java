package UtilClasses;

import Controllers.NewDocumentCreatorController;

import java.io.*;

public class LoadFile implements Runnable{
    NewDocumentCreatorController newDocumentCreatorController;
    File selectedFile;

    public LoadFile(NewDocumentCreatorController newDocumentCreatorController, File selectedFile){
        this.newDocumentCreatorController = newDocumentCreatorController;
        this.selectedFile = selectedFile;
    }

    @Override
    public void run() {
        try{
            String filename = selectedFile.getAbsolutePath();
            FileReader reader = new FileReader(filename);
            BufferedReader br = new BufferedReader(reader);

            String line;
            StringBuilder totalFile = new StringBuilder();
            long linesLoaded = 0;
            while ((line = br.readLine()) != null) {
                totalFile.append(line);
                totalFile.append("\n");
            }
            String result = totalFile.toString();
            newDocumentCreatorController.setFileContent(result);
//            System.out.println(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
