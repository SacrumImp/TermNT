package Views;

import ViewModels.FrameViewModel;

import javax.swing.*;

public class SendText extends JFrame{
    private JTextField userTextField;
    private JTextArea messageTextArea;
    private JButton sendButton;
    private JButton closeButton;
    private JLabel userLabel;
    private javax.swing.JPanel JPanelSend;
    private JTextField messageTextField;
    private JScrollPane scrollPane;

    private final String myName;

    public SendText(FrameViewModel viewModel) {
        this.getContentPane().add(JPanelSend);
        this.myName = viewModel.getUserName();

        this.userTextField.setText(viewModel.getConnectedName());

        this.sendButton.addActionListener(e -> {
            if (!messageTextField.getText().equals("")){
                messageTextArea.append(String.format("<%s>: %s\n", myName, messageTextField.getText()));
                viewModel.sendMessage(messageTextField.getText());
                messageTextField.setText("");
            }
        });

        this.closeButton.addActionListener(e -> setVisible(false));
    }

    public void cleanArea(){
        this.messageTextArea.setText("");
    }

}
