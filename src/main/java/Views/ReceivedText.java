package Views;

import ViewModels.FrameViewModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReceivedText extends JFrame{
    private javax.swing.JPanel JPanelReceived;
    private JTextField userTextField;
    private JTextArea userTextArea;
    private JButton closeButton;
    private JLabel userLabel;
    private JScrollPane scrollPane;

    private FrameViewModel viewModel;

    public ReceivedText(FrameViewModel viewModel) {
        this.getContentPane().add(JPanelReceived);
        this.viewModel = viewModel;

        this.userTextField.setText(this.viewModel.getConnectedName());

        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
    }

}
