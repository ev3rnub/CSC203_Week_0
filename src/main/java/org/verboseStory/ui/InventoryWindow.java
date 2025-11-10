package org.verboseStory.ui;

//std
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

//NOTE: see GameWindow.Java comments for detailed comments, it mostly duplicates most of the code.
// Inventory Window
public final class InventoryWindow extends JFrame {

    private final JTextPane inventoryPane;
    private final BlockingQueue<String> inventoryQueue;

    public InventoryWindow(BlockingQueue<String> inventoryQueue, String titleSuffix) {
        super("Verbose Hominid v0.0.1: " + titleSuffix);
        this.inventoryQueue = inventoryQueue;

        setSize(680, 420);
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);
        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBackground(Color.BLACK);
        setContentPane(main);

        inventoryPane = new JTextPane();
        inventoryPane.setEditable(false);
        inventoryPane.setBackground(Color.BLACK);
        inventoryPane.setForeground(Color.WHITE);
        inventoryPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        inventoryPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JScrollPane scroll = new JScrollPane(inventoryPane);
        scroll.getViewport().setBackground(Color.BLACK);
        main.add(scroll, BorderLayout.CENTER);
    }

    void appendToWindow(Color color, String text) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = inventoryPane.getStyledDocument();
            Style style = inventoryPane.addStyle("color", null);
            StyleConstants.setForeground(style, color);
            try {
                doc.insertString(doc.getLength(), text + "\n", style);
                inventoryPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }
}