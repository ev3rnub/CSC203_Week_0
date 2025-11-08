package org.verboseStory.ui;

import org.verboseStory.engine.GameEngine;
import org.verboseStory.engine.GameEngineStaticHolder;
import org.verboseStory.ui.Scene;
import org.verboseStory.ui.Inventory;
import org.verboseStory.ui.SceneWindow;
import org.verboseStory.ui.InventoryWindow;
import org.verboseStory.engine.RegexEngine;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.BlockingQueue;

/**
 * Main chat window – contains the main chat pane and the input controls.
 */
public final class GameWindow extends JFrame {

    private final JTextPane logPane;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JButton sceneButton;
    private final JButton inventoryButton;
    private final BlockingQueue<String> inputQueue;

    public GameWindow(BlockingQueue<String> inputQueue) {
        super("Verbose Hominid v0.0.1: Story Master");
        this.inputQueue = inputQueue;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 780);
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);
        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBackground(Color.BLACK);
        setContentPane(main);

        // ---- Log pane -------------------------------------------------
        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setBackground(Color.BLACK);
        logPane.setForeground(Color.WHITE);
        logPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        logPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        JScrollPane scroll = new JScrollPane(logPane);
        scroll.getViewport().setBackground(Color.BLACK);
        main.add(scroll, BorderLayout.CENTER);

        // ---- Input panel -----------------------------------------------
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.BLACK);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.WHITE));

        inputField = new JTextField();
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        inputField.setBackground(Color.DARK_GRAY);
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        sendButton = createButton("Send");
        sceneButton = createButton("Scene");
        inventoryButton = createButton("Inventory");

        inputPanel.add(inputField);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(sendButton);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(sceneButton);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(inventoryButton);
        main.add(inputPanel, BorderLayout.SOUTH);

        // ---- Event wiring ------------------------------------------------
        ActionListener send = e -> {
            String line = inputField.getText().trim();
            if (!line.isEmpty()) {
                inputQueue.offer(line);
                inputField.setText("");
            }
        };
        sendButton.addActionListener(send);
        inputField.addActionListener(send);

        sceneButton.addActionListener(e -> {
            SceneWindow w = Scene.getWindow();
            if (w != null) w.setVisible(!w.isVisible());
            else GameEngine.red_chat_output("SceneWindow not initialized");
        });

        inventoryButton.addActionListener(e -> {
            InventoryWindow w = Inventory.getWindow();
            if (w != null) w.setVisible(!w.isVisible());
            else GameEngine.red_chat_output("InventoryWindow not initialized");
        });

        // Auto‑focus the input field when the window appears.
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) {
                inputField.requestFocusInWindow();
            }
        });
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(0x2A2A2A));
        b.setForeground(Color.BLACK);
        return b;
    }

    // Called by {@link ChatWindow}
    void appendChat(Color color, String text) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = logPane.getStyledDocument();
            Style style = logPane.addStyle("color", null);
            StyleConstants.setForeground(style, color);
            try {
                doc.insertString(doc.getLength(), text + "\n", style);
                logPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }
}