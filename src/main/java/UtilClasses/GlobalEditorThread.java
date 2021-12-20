package UtilClasses;

import Controllers.GlobalEditorController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class GlobalEditorThread implements Runnable{
    Socket socket;
    GlobalEditorController globalEditorController;

    public GlobalEditorThread(Socket socket, GlobalEditorController globalEditorController){
        this.socket = socket;
        this.globalEditorController = globalEditorController;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                int choice = (int) oi.readInt();
                if(choice==1000) break;
                switch (choice){
                    case 1:
                        //get personal message from the client
                        String message = oi.readUTF();
                        globalEditorController.personalChatTextArea.appendText(message + "\n");
                        break;
                    case 2:
                        //acknowledgement from server
                        String acknowledgementFromServer = oi.readUTF();
                        System.out.println(acknowledgementFromServer);
                        break;
                    case 3:
                        //get room message
                        String roomMessage = oi.readUTF();
                        globalEditorController.roomChatTextArea.appendText(roomMessage + "\n");
                        break;
                    default:
                        break;
                }

            } catch (IOException e) { e.printStackTrace();}
        }
        System.out.println("Thread interrupted");
    }
}
