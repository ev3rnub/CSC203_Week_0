package org.verboseStory.ui;
//my classes
import org.verboseStory.engine.GameEngine;
//std
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.BlockingQueue;

//main npte window and the input controls/buttons, window title and provides
// a method to append chat to our note window.
public final class NoteWindow extends JFrame {
    private String NWTITLE = "NOTEWINDOW";
    // JtextPane is a text component that can be marked up with attributes that are represented graphically.
    private final JTextPane notePane;
    private final JButton saveButton;
    // the queue isnt realy used in our notes, however we still can send messages to the game engine, so I left it here
    // for future use.
    private final BlockingQueue<String> inputQueue;

    // l
    private StringBuffer sessionNote = new StringBuffer();

    // Game widow constructor, defines all the things our player observes.
    public NoteWindow(BlockingQueue<String> inputQueue, String someTitle) {
        // window title
        super("Verbose Hominid:SunHome13 v0.0.4: " + someTitle);
        // define queue
        this.inputQueue = inputQueue;
        // set window size
        setSize(600, 480);
        // instance a new JPanel with a new border layout
        JPanel main = new JPanel(new BorderLayout(5, 5));
        // define its background color
        main.setBackground(Color.BLACK);
        //set the content to main.
        setContentPane(main);

        // Define readable area.
        notePane = new JTextPane();
        // disable so the player can't edit the output.
        notePane.setEditable(true);
        notePane.setCaretColor(Color.WHITE);
        // define the background color
        notePane.setBackground(Color.BLACK);
        // set the foreground color
        notePane.setForeground(Color.WHITE);
        notePane.setMargin(new Insets(5, 5, 5, 5));
        // define font type
        notePane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        // define window border
        notePane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        // instance a scroll pane.
        JScrollPane scroll = new JScrollPane(notePane);
        // define its background color.
        scroll.getViewport().setBackground(Color.BLACK);
        //add it to our main
        main.add(scroll, BorderLayout.CENTER);

        // Instance a new JPanel
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.BLACK);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.WHITE));

        // define our buttons
        saveButton = createButton("Save");
        inputPanel.add(saveButton);
        inputPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // add the input panel with our button to main
        main.add(inputPanel, BorderLayout.SOUTH);


        ActionListener save = e -> {
            String line = notePane.getText().trim();
            if (!sessionNote.isEmpty()){
                sessionNote.delete(0, sessionNote.length());
            }
            if (!line.isEmpty()) {
                sessionNote.append(line);
                System.out.println(sessionNote.toString());
                System.out.println("NoteSaved");
                NoteWindow aNote = Note.getWindow();
                if (aNote.isVisible()) {
                    aNote.setVisible(false);
                }
            }
        };


        // on send button press offer input
        saveButton.addActionListener(save);

        // Auto‑focus the input field when the window appears.
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) {
                notePane.requestFocusInWindow();
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

    // Called by game to insert a line into our game window.
    void saveNote() {
        SwingUtilities.invokeLater(() -> {
            // get current notePanes styled document, and assign it to doc
            StyledDocument doc = notePane.getStyledDocument();
            // define a new style
            Style style = notePane.addStyle("color", null);
            // set foreground style and color.
            StyleConstants.setForeground(style, Color.WHITE);

            String someNotes = notePane.getText();
            if (!someNotes.equals("")) {
                GameEngine.printOutput(Color.CYAN, NWTITLE, someNotes);
            }
        });
    }
}