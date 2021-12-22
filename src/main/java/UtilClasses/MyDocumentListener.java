package UtilClasses;

import javafx.scene.control.TextArea;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class MyDocumentListener implements DocumentListener {
    public TextArea lines;
    public MyDocumentListener(TextArea lines){
        this.lines = lines;
    }

    String newline = "\n";
    public String getText(DocumentEvent e) {
        Document doc = (Document)e.getDocument();
        int caretPosition = doc.getLength();
//        int changeLength = e.getLength();
        Element root = doc.getDefaultRootElement();
        StringBuilder text = new StringBuilder("1" + System.getProperty("line.separator"));
        for(int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
            text.append(i).append(System.getProperty("line.separator"));
        }
        return text.toString();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        lines.setText(getText(e));
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        lines.setText(getText(e));
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        lines.setText(getText(e));
    }
}