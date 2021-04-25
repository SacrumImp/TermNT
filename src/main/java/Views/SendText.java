package Views;

import ViewModels.FrameViewModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendText extends JFrame{
    private JTextField userTextField;
    private JTextArea messageTextArea;
    private JButton sendButton;
    private JButton closeButton;
    private JLabel userLabel;
    private javax.swing.JPanel JPanelSend;
    private JTextField messageTextField;

    private FrameViewModel viewModel;

    public SendText(FrameViewModel viewModel) {
        this.getContentPane().add(JPanelSend);
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
