package Views;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecievedText extends JFrame{
    private javax.swing.JPanel JPanelRecieved;
    private JTextField userTextField;
    private JTextArea userTextArea;
    private JButton closeButton;
    private JLabel userLabel;

    public RecievedText() {
        this.getContentPane().add(JPanelRecieved);

        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
    }

}
