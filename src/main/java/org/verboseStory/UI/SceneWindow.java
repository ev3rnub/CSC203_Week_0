package org.verboseStory.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

/**
 * Separate, scrollable window that displays scene history text.
 */
public final class SceneWindow extends JFrame {

    private final JTextPane scenePane;
    private final BlockingQueue<String> sceneQueue;

    public SceneWindow(BlockingQueue<String> sceneQueue) {
        super("Verbose Hominid v0.0.1: SCENE History");
        this.sceneQueue = sceneQueue;

        setSize(680, 420);
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);
        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBackground(Color.BLACK);
        setContentPane(main);

        scenePane = new JTextPane();
        scenePane.setEditable(false);
        scenePane.setBackground(Color.BLACK);
        scenePane.setForeground(Color.WHITE);
        scenePane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        scenePane.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JScrollPane scroll = new JScrollPane(scenePane);
        scroll.getViewport().setBackground(Color.BLACK);
        main.add(scroll, BorderLayout.CENTER);
    }

    void appendScene(Color color, String text) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = scenePane.getStyledDocument();
            Style style = scenePane.addStyle("color", null);
            StyleConstants.setForeground(style, color);
            try {
                doc.insertString(doc.getLength(), text + "\n", style);
                scenePane.setCaretPosition(doc.getLength());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }
}