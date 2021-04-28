package Views;

import ViewModels.FrameViewModel;
import com.fazecast.jSerialComm.SerialPort;
import enums.Baud;
import enums.DataBits;
import enums.Parity;
import enums.StopBits;

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
    public static final Color VERY_DARK_RED = new Color(187, 0, 0);

    private SendText sendText;
    private ReceivedText receivedText;

    FrameViewModel viewModel;

    public MainWindow(){
        this.getContentPane().add(formBlock);

        this.viewModel = new FrameViewModel();

        this.viewModel.setMainWindowUIInterface(new MainWindowUI() {
            @Override
            public void changeLogicalConnectLabel() {
                status.setText("Подключено");
                status.setForeground(VERY_DARK_GREEN);

                buttonOpenChat.setEnabled(true);
                buttonDisconnect.setEnabled(true);
                buttonParam.setEnabled(true);

                textFieldName.setEditable(false);
                buttonConnect.setEnabled(false);

                sendText = new SendText(viewModel);
                sendText.setTitle("Исходящие");
                sendText.pack();
                sendText.setSize(500,350);

                receivedText = new ReceivedText(viewModel);
                receivedText.setTitle("Входящие");
                receivedText.pack();
                receivedText.setSize(500,300);
            }

            @Override
            public void changeComPortParams(int speed, int bits, int stopBits, int parity){
                comboBoxSpeed.setSelectedIndex(speed);
                comboBoxBits.setSelectedIndex(bits);
                comboBoxStopBits.setSelectedIndex(stopBits);
                comboBoxParity.setSelectedIndex(parity);

            }

            @Override
            public void uiAfterDisconnect() {
                changeUIAfterDisconnect();
            }

            @Override
            public String setUserName() {
                return textFieldName.getText();
            }
        });

        this.configureUI();

        //этап установки физического соединения
        this.comboBoxPort.addActionListener(e -> {
            this.comboBoxPort.setEnabled(false);
            int ind = comboBoxPort.getSelectedIndex() - 1;

            if(ind < 0) return;

            this.viewModel.setPort(ind);
            //старт потока для установки физического соединения
            PhysicalConnectionThread physicalConnectionThread = new PhysicalConnectionThread();
            Thread thread = new Thread(physicalConnectionThread);
            thread.start();
        });

        this.buttonConnect.addActionListener(e -> {
            this.viewModel.setUserName(this.textFieldName.getText());
            this.viewModel.setLogicalConnection();
        });

        this.buttonDisconnect.addActionListener(e -> {
            this.viewModel.disconnectAll();
            changeUIAfterDisconnect();
        });

        this.buttonOpenChat.addActionListener(e -> {
            this.sendText.setVisible(true);
            this.receivedText.setVisible(true);
        });

        this.buttonParam.addActionListener(e -> viewModel.setComPortParams(comboBoxSpeed.getSelectedIndex(), comboBoxBits.getSelectedIndex(),
                comboBoxStopBits.getSelectedIndex(), comboBoxParity.getSelectedIndex()));

    }

    public void configureUI(){

        this.comboBoxPort.addItem("Порт не выбран");
        for (SerialPort port : this.viewModel.getPorts()) {
            this.comboBoxPort.addItem(port.getPortDescription());
        }

        for (Baud param : Baud.values())
            this.comboBoxSpeed.addItem(param.getSpeed());

        for (DataBits param : DataBits.values())
            this.comboBoxBits.addItem(param.getBitsNum());

        for(StopBits param : StopBits.values())
            this.comboBoxStopBits.addItem(param.getStopBits());

        for (Parity param : Parity.values())
            this.comboBoxParity.addItem(param.getStatus());
    }

    public void changeUIAfterDisconnect(){
        this.buttonParam.setEnabled(false);

        this.comboBoxPort.setSelectedIndex(0);
        this.comboBoxPort.setEnabled(true);

        this.status.setText("Отключено");
        this.status.setForeground(VERY_DARK_RED);

        this.textFieldName.setEditable(true);

        this.buttonDisconnect.setEnabled(false);
        this.buttonOpenChat.setEnabled(false);

        this.comboBoxSpeed.setSelectedIndex(0);
        this.comboBoxBits.setSelectedIndex(0);
        this.comboBoxStopBits.setSelectedIndex(0);
        this.comboBoxParity.setSelectedIndex(0);

        if (this.sendText != null){
            this.sendText.cleanArea();
            this.sendText.setVisible(false);
        }
        if (this.receivedText != null){
            this.receivedText.cleanArea();
            this.receivedText.setVisible(false);
        }
    }

    //Поток физического соединения
    class PhysicalConnectionThread implements Runnable {
        @Override
        public void run() {
            boolean result = viewModel.setPhysicalConnection();
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
