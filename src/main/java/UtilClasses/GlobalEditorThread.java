package UtilClasses;

import Controllers.GlobalEditorController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
                    case 4:
                        //get room details
                        globalEditorController.userAccessInfoArrayList = (ArrayList<UserAccessInfo>) oi.readObject();
                        globalEditorController.userList = (ArrayList<String>) oi.readObject();
                        break;
                    case 5:
                        //get all users
                        globalEditorController.userList = (ArrayList<String>) oi.readObject();
                        break;
                    case 6:
                        //get caretPosition of any online user working in same roomId;
                        int userId = oi.readInt();
                        int caretPosition = oi.readInt();
                        globalEditorController.usersCaretPosition.put(userId, caretPosition);
                        SetCaretTextArea setCaretTextArea = new SetCaretTextArea(globalEditorController);
                        Thread thread = new Thread(setCaretTextArea);
                        thread.start();
//                        globalEditorController.usersCaretPosition.forEach((userId1, caretPosition1) -> {
//                            System.out.println(userId1 + " -> " + caretPosition1);
//                        });
                        break;
                    case 7:
                        HashMap<Integer, RoomUser> roomUserHashMap = (HashMap<Integer, RoomUser>) oi.readObject();
                        String roomContent = oi.readUTF();
                        globalEditorController.documentDetails.setDocumentContent(roomContent);
                        globalEditorController.documentContentTextArea.setText(roomContent);

                        roomUserHashMap.forEach((userId2, roomUserDetail) -> {
                            globalEditorController.usersCaretPosition.put(userId2, roomUserDetail.getCaretPosition());
                        });

                        SetCaretTextArea setCaretTextArea1 = new SetCaretTextArea(globalEditorController);
                        Thread thread1 = new Thread(setCaretTextArea1);
                        thread1.start();
//                        roomUserHashMap.forEach((userId2, roomUserDetail) -> {
//                            globalEditorController.usersCaretPosition.put(userId2, roomUserDetail.getCaretPosition());
//                        });
                        break;
                    case 8:
                        int userUpdated = oi.readInt();
                        String roomContent1 = oi.readUTF();
//                        System.out.println(roomContent1);
                        globalEditorController.documentDetails.setDocumentContent(roomContent1);
                        globalEditorController.documentContentTextArea.setText(roomContent1);
                        break;
                    default:
                        break;
                }

            } catch (IOException | ClassNotFoundException e) { e.printStackTrace();}
        }
        System.out.println("Thread interrupted");
    }
}
