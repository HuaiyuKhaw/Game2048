package game2048;

import Databases.Controller;
import Databases.Data;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

class HighScore {
    // Add Two Panel
    private JPanel outerPanel = new JPanel();
    private JPanel innerPanel = new JPanel();

    HighScore() {
        // Initialize 2 panel
        outerPanel.setLayout(new BorderLayout());
        innerPanel.setLayout(new GridLayout(0, 2));

        // Customize the panel and component
        outerPanel.setBackground(Color.decode("#9E9E9E"));
        innerPanel.setBackground(Color.decode("#9E9E9E"));
        JLabel title = new JLabel("HIGH SCORE", JLabel.CENTER);
        customizeText(title, false);

        // Add Nested Panel and Button into outerPanel
        outerPanel.add(title, BorderLayout.PAGE_START);
        populate();
        outerPanel.add(innerPanel, BorderLayout.CENTER);
        JButton back = new JButton("Back");
        outerPanel.add(back, BorderLayout.PAGE_END);

        // Return to Main Page Listener
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                outerPanel.removeAll();
                outerPanel.repaint();
                outerPanel.revalidate();
                outerPanel.add(new Introduction().getMain_panel());
                outerPanel.repaint();
                outerPanel.revalidate();
            }
        });
    }

    private void customizeText(JLabel jLabel, boolean isBody) {
        if (isBody) {
            Font font = new Font("Roboto", Font.PLAIN, 16);
            Border paddingBorder = BorderFactory.createEmptyBorder();

            jLabel.setBorder(paddingBorder);
            jLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            jLabel.setFont(font);
        } else {
            Font font = new Font("Roboto", Font.PLAIN, 48);
            Border paddingBorder = BorderFactory.createEmptyBorder(60, 60, 60, 60);
            jLabel.setBorder(paddingBorder);
            jLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            jLabel.setFont(font);
        }
    }

    private void populate() {
        Controller controller = new Controller();
        List<Data> database = controller.getArrayList();
        for (Data data : database) {
            if (database.indexOf(data) == 10) {
                break;
            }
            if (database.indexOf(data) == 0) {
                addText("NAME");
                addText("SCORE");
            }

            addText(data.name);
            addText(Integer.toString(data.score));

        }
    }

    private void addText(String text) {
        JLabel name = new JLabel(text, JLabel.CENTER);
        customizeText(name, true);
        innerPanel.add(name);
    }

    JPanel getOuterPanel() {
        return outerPanel;
    }
}
