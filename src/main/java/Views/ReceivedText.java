package Views;

import ViewModels.FrameViewModel;

import javax.swing.*;

public class ReceivedText extends JFrame{
    private javax.swing.JPanel JPanelReceived;
    private JTextField userTextField;
    private JTextArea messageTextArea;
    private JButton closeButton;
    private JLabel userLabel;
    private JScrollPane scrollPane;

    private FrameViewModel viewModel;

    public ReceivedText(FrameViewModel viewModel) {
        this.getContentPane().add(JPanelReceived);

        this.userTextField.setText(viewModel.getConnectedName());

        viewModel.setReceivedMessageUIInterface(text -> messageTextArea.append(String.format("<%s>: %s\n", viewModel.getConnectedName(), text)));

        this.closeButton.addActionListener(e -> setVisible(false));
    }

    public void cleanArea(){
        this.messageTextArea.setText("");
    }

}
