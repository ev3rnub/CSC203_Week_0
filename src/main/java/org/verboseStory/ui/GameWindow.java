package org.verboseStory.ui;

//my classes
import org.verboseStory.engine.GameEngine;
import org.verboseStory.engine.SoundEngine;
//std
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.BlockingQueue;

import org.verboseStory.engine.SoundEngine;
import org.verboseStory.ui.Note;

//main game window and the input controls/buttons, window title and provides
// a method to append chat to our game window.
public final class GameWindow extends JFrame {
    // JtextPane is a text component that can be marked up with attributes that are represented graphically.
    private final JTextPane logPane;
    // JTextField is a lightweight component that allows the editing of a single line of text. Its our input.
    private final JTextField inputField;
    // buttons
    private final JButton sendButton;
    private final JButton sceneButton;
    private final JButton inventoryButton;
    private final JButton noteButton;
    private final JButton stopMusicButton;
    private final JButton saveGameButton;
    // this creates the queue we use to move messages from the user, and from the game engine to the UI. This allows
    // the engine to block when we take from the queue.
    private final BlockingQueue<String> inputQueue;

    // Game widow constructor, defines all the things our player observes.
    public GameWindow(BlockingQueue<String> inputQueue) {
        // window title
        super("Verbose Hominid:SunHome13 v0.0.4: Story Master");
        // define queue
        this.inputQueue = inputQueue;
        // on close, exit completely
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // set window size
        setSize(1200, 780);
        // instance a new JPanel with a new border layout
        JPanel main = new JPanel(new BorderLayout(5, 5));
        // define its background color
        main.setBackground(Color.BLACK);
        //set the content to main.
        setContentPane(main);

        // Define readable area.
        logPane = new JTextPane();
        // disable so the player can't edit the output.
        logPane.setEditable(false);
        // define the background color
        logPane.setBackground(Color.BLACK);
        // set the foreground color
        logPane.setForeground(Color.WHITE);
        // define font type
        logPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        // define window border
        logPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        // instance a scroll pane.
        JScrollPane scroll = new JScrollPane(logPane);
        // define its background color.
        scroll.getViewport().setBackground(Color.BLACK);
        //add it to our main
        main.add(scroll, BorderLayout.CENTER);

        // Instance a new JPanel for text input
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.BLACK);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.WHITE));

        // Define a text field for our new JPanel
        inputField = new JTextField();
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.CYAN);
        inputField.setCaretColor(Color.CYAN);
        inputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        // define our buttons
        sendButton = createButton("Send");
        sceneButton = createButton("Scene");
        inventoryButton = createButton("Inventory");
        noteButton = createButton("Note");
        stopMusicButton = createButton("Stop Music");
        saveGameButton = createButton("Save Game");

        // add our objects to input panel
        inputPanel.add(inputField);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(sendButton);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(sceneButton);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(inventoryButton);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(noteButton);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(stopMusicButton);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        inputPanel.add(saveGameButton);
        inputPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        // add the input panel to main
        main.add(inputPanel, BorderLayout.SOUTH);

        // This defines our send listener for inputted text. If line isn't empty, send it to the input queue to be consumed if applicable.
        ActionListener send = e -> {
            String line = inputField.getText().trim();
            if (!line.isEmpty()) {
                inputQueue.offer(line);
                inputField.setText("");
            }
        };

        //toggles music
        ActionListener stopMusic = e -> {
            if (stopMusicButton.getText().equals("Stop Music")) {
                SoundEngine.stopMusic();
                stopMusicButton.setText("Play Music");
            }  else {
                SoundEngine.playMusic();
                stopMusicButton.setText("Stop Music");
            }
        };
        ActionListener saveGame = e -> {
            if (saveGameButton.getText().equals("Save Game")) {
                GameEngine.SaveGame();
            }
        };
        // on send button press offer input
        sendButton.addActionListener(send);
        // on enter offer input
        inputField.addActionListener(send);
        stopMusicButton.addActionListener(stopMusic);
        saveGameButton.addActionListener(saveGame)
        // Defines a action listener for the sceneButton, and when pressed it either hides, or unhides the
        // scene history window.
        sceneButton.addActionListener(e -> {
            SceneWindow w = Scene.getWindow();
            if (w != null) w.setVisible(!w.isVisible());
            else GameEngine.red_chat_output("SceneWindow not initialized");
        });
        // Defines a action listener for the inventoryButton, and when pressed it either hides, or unhides the
        // inventory window.
        inventoryButton.addActionListener(e -> {
            InventoryWindow w = Inventory.getWindow();
            if (w != null) w.setVisible(!w.isVisible());
            else GameEngine.red_chat_output("InventoryWindow not initialized");
        });
        // defines a action listener for noteButton and when pressed it either hides or unhides the note window.
        noteButton.addActionListener(e -> {
            NoteWindow w = Note.getWindow();
            if (w != null) w.setVisible(!w.isVisible());
            else GameEngine.red_chat_output("NoteWindow not initialized");
        });

        saveGameButton.addActionListener(e -> {
            if (saveGameButton.getText().equals("Save Game")) {
                GameEngine.SaveGame();
            }
        })
        // Auto‑focus the input field when the window appears.
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) {
                inputField.requestFocusInWindow();
            }
        });
    }
    // basic helper function to ensure buttons we create are all of similar style.
    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(0x2A2A2A));
        b.setForeground(Color.BLACK);
        return b;
    }

    // Called by ChatWindow to insert a line into our game window.
    void appendChat(Color color, String text) {
        SwingUtilities.invokeLater(() -> {
            // get current logPanes styled document, and assign it to doc
            StyledDocument doc = logPane.getStyledDocument();
            // define a new style
            Style style = logPane.addStyle("color", null);
            // set foreground style and color.
            StyleConstants.setForeground(style, color);
            // try and insert text into doc at its last index, if error catch it and print to stack trace for debugging.
            try {
                doc.insertString(doc.getLength(), text + "\n", style);
                logPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }
}