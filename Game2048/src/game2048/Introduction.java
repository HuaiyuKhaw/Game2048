package game2048;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

//Written by someone

class Introduction {

    // Initialize a new panel
    private JPanel main_panel = new JPanel();
    private JPanel second_panel = new JPanel();
    // Add Components
    private JButton start = new JButton("Start");
    private JTextField input = new JTextField(10);
    private JTextField row = new JTextField(1);
    private JTextField column = new JTextField(1);

    Introduction(){
        // Add Layout
        main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.PAGE_AXIS));
        main_panel.setBackground(Color.decode("#9E9E9E"));

        second_panel.setLayout(new GridLayout(1, 4));
        second_panel.add(new JLabel("ROW", JLabel.CENTER));
        second_panel.add(customizeTextField(row));
        second_panel.add(new JLabel("COLUMN", JLabel.CENTER));
        second_panel.add(customizeTextField(column));
        second_panel.setMaximumSize(new Dimension(300,36));
        // Customize and add components
        main_panel.add(customizeTextLabel(new JLabel("2048", JLabel.CENTER)));
        main_panel.add(customizeTextField(input));
        main_panel.add(Box.createRigidArea(new Dimension(0,16)));
        main_panel.add(second_panel);
        main_panel.add(Box.createRigidArea(new Dimension(0,16)));
        main_panel.add(customizeButton(start, true));
        main_panel.add(customizeButton(new JButton("High Score"), false));
    }

    JPanel getMain_panel() {
        return main_panel;
    }

    // Customize TextField and return it
    private JTextField customizeTextField(JTextField textField){
        Font font = new Font("Roboto", Font.PLAIN, 24);
        textField.setFont(font);
        textField.setMaximumSize(textField.getPreferredSize());
        textField.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);


        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            void changed(){
                if(textField.getText().equals("")){
                    start.setEnabled(false);
                }
                else{
                    start.setEnabled(true);
                }
            }
        });
        return textField;
    }

    // Customize Button and return it
    private JButton customizeButton(JButton jButton, boolean isStart){
        Font font = new Font("Verdana", Font.BOLD, 24);

        jButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        jButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        jButton.setFont(font);
        if(isStart){
            jButton.addActionListener(e -> {
                main_panel.removeAll();
                main_panel.repaint();
                main_panel.revalidate();

                main_panel.add(new Game2048(input.getText(), Integer.parseInt(row.getText()), Integer.parseInt(column.getText())));
                main_panel.repaint();
                main_panel.revalidate();
            });
            jButton.setEnabled(false);
        }
        else{
            jButton.addActionListener(e -> {
                main_panel.removeAll();
                main_panel.repaint();
                main_panel.revalidate();

                main_panel.add(new HighScore().getOuterPanel());
                main_panel.repaint();
                main_panel.revalidate();
            });
        }
        return jButton;
    }

    // Customize TextLabel and return it
    private JLabel customizeTextLabel(JLabel jLabel){
        Font font = new Font("Roboto", Font.PLAIN, 48);
        Border paddingBorder = BorderFactory.createEmptyBorder(100,20,100,20);

        jLabel.setBorder(paddingBorder);
        jLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        jLabel.setFont(font);
        return jLabel;
    }
}
