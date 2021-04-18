import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.*;

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

    SerialPort[] ports;
    SerialPort port;

    public MainWindow() {
        this.getContentPane().add(formBlock);

        ports = SerialPort.getCommPorts();
        this.configureUI();

        //этам установки физического соединения
        this.comboBoxPort.addActionListener(e -> {
            this.comboBoxPort.setEnabled(false);
            int ind = comboBoxPort.getSelectedIndex() - 1;

            if(ind < 0) return;

            port = ports[ind];

            //открытие COM-порта и установка сигнала DTR
            port.openPort();
            port.setDTR();

            //старт потока для установки физического соединения
            PhysicalConnectionThread physicalConnectionThread = new PhysicalConnectionThread();
            Thread thread = new Thread(physicalConnectionThread);
            thread.start();
        });

        this.buttonConnect.addActionListener(e -> {
            //port.setComPortParameters((int)comboBoxSpeed.getSelectedItem(), (int)comboBoxBits.getSelectedItem(), comboBoxStopBits.getSelectedIndex(), comboBoxParity.getSelectedIndex());
            //status.setText("Подключено");
            //status.setForeground(VERY_DARK_GREEN);
            Frame connectionFrame = new Frame(FrameTypes.LINK);
            port.writeBytes(connectionFrame.getSupervisorFrameToWrite(),
                    connectionFrame.getFrameSize());
        });

        this.buttonDisconnect.addActionListener(e -> {
            port = ports[comboBoxPort.getSelectedIndex()];
            port.closePort();

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

    public void configureUI(){

        this.comboBoxPort.addItem("Порт не выбран");
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
    }

    //Поток физического соединения
    class PhysicalConnectionThread implements Runnable {
        @Override
        public void run() {
            long end = System.currentTimeMillis() + 15000;
            while (System.currentTimeMillis() < end){
                if (port.getDSR()){
                    buttonConnect.setEnabled(true);
                    port.addDataListener(new SerialPortDataListener() {
                        @Override
                        public int getListeningEvents() {
                            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                        }

                        @Override
                        public void serialEvent(SerialPortEvent serialPortEvent) {
                            if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                                return;
                            byte[] newData = new byte[port.bytesAvailable()];
                            port.readBytes(newData, newData.length);
                            for(byte bites : newData){
                                System.out.println(bites);
                            }

                        }
                    });
                    return;
                }
            }
            comboBoxPort.setSelectedIndex(0);
            comboBoxPort.setEnabled(true);
            port.closePort();
        }
    }

}
