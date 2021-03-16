import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;

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

    public MainWindow() {
        this.getContentPane().add(formBlock);
    }

    public void setPortNames(SerialPort[] ports){
        for(SerialPort port : ports)
        this.comboBoxPort.addItem(port.getDescriptivePortName());
    }

}
