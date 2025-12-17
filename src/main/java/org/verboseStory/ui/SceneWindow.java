package org.verboseStory.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

//NOTE: see GameWindow.Java comments for detailed comments, it mostly duplicates most of the code.
// Future ME: Refactor a general window class to construct windows accordingly.
//define a class that extends from a Jframe called SceneWindow
public final class SceneWindow extends JFrame {
//    define a text pane and a blocking queue of type strings
    private final JTextPane scenePane;
    private final BlockingQueue<String> sceneQueue;

    public SceneWindow(BlockingQueue<String> sceneQueue) {
        super("Verbose Hominid v0.0.4: Scene History");
        this.sceneQueue = sceneQueue;

        setSize(680, 700);
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);
        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBackground(Color.BLACK);
        setContentPane(main);

        JButton someButton = new JButton("Define History");
        someButton.setBackground(Color.BLACK);
        someButton.setForeground(Color.WHITE);
        someButton.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        someButton.setVisible(true);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setForeground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        buttonPanel.setVisible(true);
        buttonPanel.setLayout(new SpringLayout());
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMinimumSize(new Dimension(620, 100));
        buttonPanel.add(someButton);

        scenePane = new JTextPane();
        scenePane.setEditable(false);
        scenePane.setBackground(Color.BLACK);
        scenePane.setForeground(Color.WHITE);
        scenePane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        scenePane.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JScrollPane scroll = new JScrollPane(scenePane);
        scroll.getViewport().setBackground(Color.BLACK);
        main.add(scroll, BorderLayout.CENTER);
        main.add(buttonPanel, BorderLayout.SOUTH);
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