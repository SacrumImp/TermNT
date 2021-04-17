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
    private JButton buttonParam;

    public static final Color VERY_DARK_GREEN = new Color(0, 102, 0);

    SerialPort port;

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

        this.buttonConnect.addActionListener(e -> {
            port = ports[comboBoxPort.getSelectedIndex()];
            port.setComPortParameters((int)comboBoxSpeed.getSelectedItem(), (int)comboBoxBits.getSelectedItem(), comboBoxStopBits.getSelectedIndex(), comboBoxParity.getSelectedIndex());
            blockingInterface(false);

            //открытие COM-порта и установка сигнала DTR
            port.openPort();
            port.setDTR();

            //старт потока для установки физического и логического соединения
            ConnectionThread connectionThread = new ConnectionThread();
            Thread thread = new Thread(connectionThread);
            thread.start();

        });

        this.buttonDisconnect.addActionListener(e -> {
            port = ports[comboBoxPort.getSelectedIndex()];
            port.closePort();
            blockingInterface(true);

            status.setText("Отключено");
            status.setForeground(Color.RED);

        });

        this.buttonOpenChat.addActionListener(e -> {
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
        });

        this.buttonParam.addActionListener(e -> {

        });

    }

    //функция для блокировки интерфейса при установленном соединении
    public void blockingInterface(boolean key){
        this.textFieldName.setEnabled(key);
        this.comboBoxPort.setEnabled(key);
        this.comboBoxSpeed.setEnabled(key);
        this.comboBoxBits.setEnabled(key);
        this.comboBoxStopBits.setEnabled(key);
        this.comboBoxParity.setEnabled(key);
    }

    class ConnectionThread implements Runnable
    {
        public void run()
        {
            long end = System.currentTimeMillis() + 15000;
            while (System.currentTimeMillis() < end){
                if (port.getDSR()){


                    status.setText("Подключено");
                    status.setForeground(VERY_DARK_GREEN);

                    buttonOpenChat.setEnabled(true);

                    return;
                }
            }
            comboBoxPort.setEnabled(true);
            port.closePort();
        }
    }

}
