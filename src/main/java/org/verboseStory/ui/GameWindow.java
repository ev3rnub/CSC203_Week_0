package org.verboseStory.ui;

//my classes
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.verboseStory.api.Xai_Api;
import org.verboseStory.engine.GameEngine;
import org.verboseStory.engine.SoundEngine;
//std
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import org.verboseStory.engine.SoundEngine;
import org.verboseStory.ui.Note;

//main game window and the input controls/buttons, window title and provides
// a method to append chat to our game window.
public final class GameWindow extends JFrame {
    // JtextPane is a text component that can be marked up with attributes that are represented graphically.
    public final JTextPane logPane;
    // JTextField is a lightweight component that allows the editing of a single line of text. Its our input.
    private final JTextField inputField;
    // JMenu
    private final JMenuBar gameMenuBar;
    // buttons
    private final JButton sendButton;
    private final JButton sceneButton;
    private final JButton inventoryButton;
    private final JButton noteButton;
    private final JButton stopMusicButton;
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
        setSize(1400, 780);
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
        // menu bar
        // define a JMenuBar
        gameMenuBar = new JMenuBar();
        // create Character Menu
        JMenu charMenu = new JMenu("Character");
        // create menu items for character menu
        JMenuItem charInfo = new JMenuItem("Character Info");
        charMenu.add(charInfo);
        // add our new menu, and menu items to our menu bar
        gameMenuBar.add(charMenu);
        // create about menu
        JMenu aboutMenu = new JMenu("About");
        // sub menu item for the about meni
        JMenuItem devMenuItem = new JMenuItem("Dev");
        aboutMenu.add(devMenuItem);
        // add our new menu to the menu bar
        gameMenuBar.add(aboutMenu);
        // add our newly defined items to the main window
        main.add(gameMenuBar, BorderLayout.NORTH);
        main.add(inputPanel, BorderLayout.SOUTH);

        //ACTION Listeners
        ActionListener createCharacter = e -> {
            inputQueue.offer("CREATECHARACTER");
        };

        ActionListener listCharInfo = e ->  {
            GameEngine.listCharacter();
        };

        ActionListener listDev = e -> {
            inputQueue.offer("DEV");
        };

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

        // on send button press offer input
        sendButton.addActionListener(send);
        // on enter offer input
        inputField.addActionListener(send);
        stopMusicButton.addActionListener(stopMusic);
        devMenuItem.addActionListener(listDev);
        charInfo.addActionListener(listCharInfo);

        // Defines a action listener for the sceneButton, and when pressed it either hides, or unhides the
        // scene history window.
        sceneButton.addActionListener(e -> {
            SceneWindow w = Scene.getWindow();
            if (w != null) w.setVisible(!w.isVisible());
            else GameEngine.printOutput(Color.RED, "GAME_WINDOW", "SceneWindow not initialized");
        });
        // Defines a action listener for the inventoryButton, and when pressed it either hides, or unhides the
        // inventory window.
        inventoryButton.addActionListener(e -> {
            InventoryWindow w = Inventory.getWindow();
            if (w != null) w.setVisible(!w.isVisible());
            else GameEngine.printOutput(Color.RED, "GAME_WINDOW", "InventoryWindow not initialized");
        });
        // defines a action listener for noteButton and when pressed it either hides or unhides the note window.
        noteButton.addActionListener(e -> {
            NoteWindow w = Note.getWindow();
            if (w != null) w.setVisible(!w.isVisible());
            else GameEngine.printOutput(Color.RED, "GAME_WINDOW", "NoteWindow not initialized");
        });

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
        b.setBackground(Color.BLACK);
        b.setForeground(Color.BLACK);
        return b;
    }

    // insert a line of text into our game window.
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
                //#FUTUREME NEED to check doc ength and purge accordingly
                logPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }
}