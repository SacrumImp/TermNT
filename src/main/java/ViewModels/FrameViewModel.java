package ViewModels;

import Coding.Hamming;
import Models.Frame;
import Views.MainWindowUI;
import Views.ReceivedMessageUI;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import enums.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;

public class FrameViewModel {

    private final SerialPort[] ports;

    //очередь фреймов I
    ArrayDeque<Frame> infoFramesQueue = new ArrayDeque<>();

    //интерфейсы
    private MainWindowUI mainWindowUI;
    private ReceivedMessageUI receivedMessageUI;

    //все последующие поля следует обнулять
    private SerialPort port = null;
    private String userName = "";
    private String connectedName = "";

    //флаги
    private boolean sendLogicalConnect = false;
    private byte numOfRet = 0;

    //строка буфер
    private String message = "";

    public FrameViewModel(){
        ports = SerialPort.getCommPorts();
    }

    public void setPort(int ind){
        this.port = ports[ind];

        //открытие COM-порта и установка сигнала DTR
        port.openPort();
        port.setDTR();
    }

    public SerialPort[] getPorts(){
        return this.ports;
    }

    public String getConnectedName() { return this.connectedName; }

    public String getUserName() { return this.userName; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMainWindowUIInterface(MainWindowUI uiInterface){ this.mainWindowUI = uiInterface; }
    public void setReceivedMessageUIInterface(ReceivedMessageUI uiInterface){ this.receivedMessageUI = uiInterface; }

    public boolean setPhysicalConnection(){
        long end = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < end){
            if (port.getDSR()){
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
                        processFrame(newData);
                    }
                });
                return true;
            }
        }
        port.closePort();
        return false;
    }

    public void disconnectAll(){
        Frame disconnectFrame = new Frame(FrameTypes.UNLINK);
        port.writeBytes(disconnectFrame.getFrameToWrite(),
                disconnectFrame.getFrameSize());
        this.sendLogicalConnect = false;
        this.userName = "";
        this.connectedName = "";
        this.port.removeDataListener();
        this.port.setComPortParameters(Baud.A.getSpeed(), DataBits.A.getBitsNum(), SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        this.port.clearDTR();
        this.port.closePort();

        this.port = null;
    }

    public void setLogicalConnection(){
        Frame connectionFrame = new Frame(FrameTypes.LINK, this.userName);
        port.writeBytes(connectionFrame.getFrameToWrite(),
                connectionFrame.getFrameSize());
        this.sendLogicalConnect = true;
    }

    private void processFrame(byte[] data){
        Frame frame = new Frame(data);
        switch(frame.getType()){
            case LINK:
                System.out.println("LINK");
                this.connectedName = frame.getNameString();
                this.userName = mainWindowUI.setUserName();
                mainWindowUI.changeLogicalConnectLabel();
                if (!this.sendLogicalConnect){
                    Frame connectionFrame = new Frame(FrameTypes.LINK, this.userName);
                    port.writeBytes(connectionFrame.getFrameToWrite(),
                            connectionFrame.getFrameSize());
                }
                break;
            case ACK:
                System.out.println("ACK");
                this.numOfRet = 0;
                infoFramesQueue.pollFirst();
                frame = infoFramesQueue.peekFirst();
                if (frame != null) port.writeBytes(frame.getFrameToWrite(), frame.getFrameSize());
                break;
            case PRM:
                System.out.println("PRM");
                getComPortParams(frame);
                break;
            case UNLINK:
                System.out.println("UNLINK");
                this.sendLogicalConnect = false;
                this.userName = "";
                this.connectedName = "";
                this.port.removeDataListener();
                this.port.clearDTR();
                this.port.setComPortParameters(Baud.A.getSpeed(), DataBits.A.getBitsNum(), SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                this.port.closePort();
                this.port = null;
                mainWindowUI.uiAfterDisconnect();
                break;
            case I:
                System.out.println("I");
                Hamming inputFrame = new Hamming(frame.getData());
                if (!inputFrame.decode()){
                    sendErrorFrame();
                    break;
                }
                message += new String(inputFrame.getData(), StandardCharsets.UTF_16);
                if (inputFrame.isEnd()){
                    receivedMessageUI.addReceivedMessage(message.substring(0, message.length() - 1));
                    message = "";
                }
                sendSuccessFrame();
                break;
            case RET:
                System.out.println("RET");
                this.numOfRet += 1;
                if (numOfRet < 15){
                    frame = infoFramesQueue.peekFirst();
                    port.writeBytes(frame.getFrameToWrite(), frame.getFrameSize());
                }
                else {
                    System.out.println("UNLINK");
                    this.sendLogicalConnect = false;
                    this.userName = "";
                    this.connectedName = "";
                    this.port.removeDataListener();
                    this.port.clearDTR();
                    this.port.setComPortParameters(Baud.A.getSpeed(), DataBits.A.getBitsNum(), SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                    this.port.closePort();
                    this.port = null;
                    mainWindowUI.uiAfterDisconnect();
                }
                break;
            default:
                break;
        }
    }

    public void setComPortParams(int speed, int bits, int stopBits, int parity){
        int params = 0;
        params = (params | speed) << 2;
        params = (params | bits) << 2;
        params = (params | stopBits) << 1;
        params = params | parity;

        Frame frame = new Frame(FrameTypes.PRM, (byte)params);
        port.writeBytes(frame.getFrameToWrite(),
                frame.getFrameSize());

        port.setComPortParameters(Baud.values()[speed].getSpeed(), DataBits.values()[bits].getBitsNum(), stopBits, parity);

    }

    public void getComPortParams(Frame frame){
        int params = frame.getData()[0];

        int parity = params & 1;
        int stopBits = (params & 6) >> 1;
        int bits = (params & 24) >> 3;
        int speed = (params & 224) >> 5;

        port.setComPortParameters(Baud.values()[speed].getSpeed(),
                DataBits.values()[bits].getBitsNum(), stopBits, parity);
        mainWindowUI.changeComPortParams(speed, bits, stopBits, parity);

    }

    public void sendMessage(String text) {
        text += (char)3;
        byte[] data = text.getBytes(StandardCharsets.UTF_16);
        byte[] subData;
        Frame frame;
        for (int i = 0; i < data.length;) {
            subData = new byte[Math.min(92, data.length - i)];
            System.arraycopy(data, i, subData, 0, Math.min(92, data.length - i));
            frame = new Frame(FrameTypes.I, subData);
            infoFramesQueue.add(frame);
            i += 92;
        }
        frame = infoFramesQueue.peekFirst();
        port.writeBytes(frame.getFrameToWrite(), frame.getFrameSize());
    }

    public void sendErrorFrame(){
        Frame frame = new Frame(FrameTypes.RET);
        port.writeBytes(frame.getFrameToWrite(), frame.getFrameSize());
    }

    public void sendSuccessFrame(){
        Frame frame = new Frame(FrameTypes.ACK);
        port.writeBytes(frame.getFrameToWrite(), frame.getFrameSize());
    }

}
