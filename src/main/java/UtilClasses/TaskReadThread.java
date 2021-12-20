package UtilClasses;

import Controllers.LocalEditorController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TaskReadThread implements Runnable{
    Socket socket;
    LocalEditorController localEditorController;
    ObjectInputStream oi;

    public TaskReadThread(Socket socket, LocalEditorController ec){
        this.socket = socket;
        this.localEditorController = ec;
    }


    @Override
    public void run() {
        //continuously loop it
        while (!Thread.currentThread().isInterrupted()){
            try{
                //Create object input stream
                oi = new ObjectInputStream(socket.getInputStream());
                
                //get input from the client
                String message = oi.readUTF();
                if(message.equals("STOP-THREAD")){
                    break;
                }
                if(!message.equals("SERVER-REPLY")){
                    //append message of the Text Area of UI (GUI Thread)
                    localEditorController.chatTextArea.appendText(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread interrupted");
    }
}
