import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
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
            }
        });

        this.buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SerialPort port = ports[comboBoxPort.getSelectedIndex()];
                System.out.println(port.getNumDataBits());
            }
        });
    }

}
