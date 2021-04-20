import com.fazecast.jSerialComm.SerialPort;

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

    FrameController controller;

    public MainWindow(){
        this.getContentPane().add(formBlock);

        this.controller = new FrameController();

        this.controller.setMainWindowUIInterface(new MainWindowUI() {
            @Override
            public void changeLogicalConnectLabel() {
                status.setText("Подключено");
                status.setForeground(VERY_DARK_GREEN);
            }
        });

        this.configureUI();

        //этам установки физического соединения
        this.comboBoxPort.addActionListener(e -> {
            this.comboBoxPort.setEnabled(false);
            int ind = comboBoxPort.getSelectedIndex() - 1;

            if(ind < 0) return;

            this.controller.setPort(ind);
            //старт потока для установки физического соединения
            PhysicalConnectionThread physicalConnectionThread = new PhysicalConnectionThread();
            Thread thread = new Thread(physicalConnectionThread);
            thread.start();
        });

        this.buttonConnect.addActionListener(e -> {
            this.controller.setUserName(this.textFieldName.getText());
            this.textFieldName.setEnabled(false);
            this.controller.setLogicalConnection();
        });

        this.buttonDisconnect.addActionListener(e -> {

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
        for (SerialPort port : this.controller.getPorts()) {
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
            boolean result = controller.setPhysicalConnection();
            if (result){
                buttonConnect.setEnabled(true);
            }
            else{
                comboBoxPort.setSelectedIndex(0);
                comboBoxPort.setEnabled(true);
            }
        }
    }

}
