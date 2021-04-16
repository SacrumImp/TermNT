import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame{

    private JPanel formBlock;
    private JTextField textFieldName;
    private JLabel labelName;
    private JLabel labelCOMPort;
    private JComboBox comboBoxPort;
    private JComboBox comboBoxSpeed;
    private JLabel labelSpeed;
    private JComboBox comboBoxBits;
    private JLabel labelBits;
    private JLabel labelStopBits;
    private JComboBox comboBoxStopBits;
    private JLabel labelParity;
    private JComboBox comboBoxParity;
    private JLabel status;
    private JLabel labelStatus;
    private JButton buttonConnect;
    private JButton buttonOpenChat;
    private JButton buttonDisconnect;
    private JButton paramButton;

    public MainWindow() {
        this.getContentPane().add(formBlock);

        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            this.comboBoxPort.addItem(port.getPortDescription());
        }

        int[] baud = {9600};
        for (int param : baud)
            this.comboBoxSpeed.addItem(param);

        int[] dataBits = {8, 7, 6, 5};
        for (int param : dataBits)
            this.comboBoxBits.addItem(param);

        String[] stopBits = {"1", "1,5", "2"};
        for(String param : stopBits)
            this.comboBoxStopBits.addItem(param);

        String[] parity = {"Нет", "Да"};
        for (String param : parity)
            this.comboBoxParity.addItem(param);

        this.buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SerialPort port = ports[comboBoxPort.getSelectedIndex()];
                port.setComPortParameters((int)comboBoxSpeed.getSelectedItem(), (int)comboBoxBits.getSelectedItem(), comboBoxStopBits.getSelectedIndex(), comboBoxParity.getSelectedIndex());
                blockingInterface(false);
                port.openPort();

                //открытие окна для отправления сообщений
                SendText sendText = new SendText();
                sendText.setTitle("Исходящие");
                sendText.pack();
                sendText.setVisible(true);

                //открытие окна для получения сообщений
                RecievedText recievedText = new RecievedText();
                recievedText.setTitle("Входящие");
                recievedText.pack();
                recievedText.setVisible(true);
            }
        });

        this.buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SerialPort port = ports[comboBoxPort.getSelectedIndex()];
                port.closePort();
                blockingInterface(true);
            }
        });
    }

    public void blockingInterface(boolean key){
        this.textFieldName.setEnabled(key);
        this.comboBoxPort.setEnabled(key);
        this.comboBoxSpeed.setEnabled(key);
        this.comboBoxBits.setEnabled(key);
        this.comboBoxStopBits.setEnabled(key);
        this.comboBoxParity.setEnabled(key);
    }

}
