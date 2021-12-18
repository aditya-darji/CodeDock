package UtilClasses;

import Editor.EditorController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TaskReadThread implements Runnable{
    Socket socket;
    EditorController editorController;
    ObjectInputStream oi;

    public TaskReadThread(Socket socket, EditorController ec){
        this.socket = socket;
        this.editorController = ec;
    }


    @Override
    public void run() {
        //continuously loop it
        while (true){
            try{
                //Create object input stream
                oi = new ObjectInputStream(socket.getInputStream());

                //get input from the client
                String message = oi.readUTF();
                if(!message.equals("SERVER-REPLY")){
                    //append message of the Text Area of UI (GUI Thread)
                    editorController.chatTextArea.appendText(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
