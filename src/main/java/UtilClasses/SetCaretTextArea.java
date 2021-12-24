package UtilClasses;

import Controllers.GlobalEditorController;

import java.util.HashMap;

public class SetCaretTextArea implements Runnable{
    GlobalEditorController globalEditorController;

    public SetCaretTextArea(GlobalEditorController globalEditorController){
        this.globalEditorController = globalEditorController;
    }

    @Override
    public void run() {
        globalEditorController.caretTextArea.clear();
        globalEditorController.usersCaretPosition.forEach((userId1, caretPosition1) -> {
            globalEditorController.caretTextArea.appendText(userId1 + " -> " + caretPosition1 + "\n");
        });
    }
}
