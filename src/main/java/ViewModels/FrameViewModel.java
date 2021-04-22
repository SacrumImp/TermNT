import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import enums.Baud;
import enums.DataBits;
import enums.FrameTypes;

public class FrameViewModel {

    private SerialPort[] ports;
    private MainWindowUI mainWindowUI;

    //все последующие поля следует обнулять
    private SerialPort port = null;
    private String userName = "";
    private String connectedName = "";
    //флаги
    private int sendLogicalConnect = 0;

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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMainWindowUIInterface(MainWindowUI uiInterface){ this.mainWindowUI = uiInterface; }

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

    public void setLogicalConnection(){
        Frame connectionFrame = new Frame(FrameTypes.LINK);
        port.writeBytes(connectionFrame.getSupervisorFrameToWrite(),
                connectionFrame.getFrameSize());
        this.sendLogicalConnect = 1;
    }

    private String processFrame(byte[] data){
        Frame frame = new Frame(data);
        System.out.println(frame.getType());
        switch(frame.getType()){
            case LINK:
                System.out.println("LINK");
                this.connectedName = frame.getDataString();
                mainWindowUI.changeLogicalConnectLabel();
                if (this.sendLogicalConnect == 0){
                    Frame connectionFrame = new Frame(FrameTypes.LINK);
                    port.writeBytes(connectionFrame.getSupervisorFrameToWrite(),
                            connectionFrame.getFrameSize());
                }
                return "Подключено";
            case ACK:
                System.out.println("ACK");
                return "";
            case PRM:
                System.out.println("PRM");
                getComPortParams(frame);
                return "";
            default:
                return "";
        }
    }

    public void setComPortParams(int speed, int bits, int stopBits, int parity){
        port.setComPortParameters(Baud.values()[speed].getSpeed(), DataBits.values()[bits].getBitsNum(), stopBits, parity);

        int params = 0;
        params = (params | speed) << 2;
        params = (params | bits) << 2;
        params = (params | stopBits) << 1;
        params = params | parity;

        Frame frame = new Frame(FrameTypes.PRM, (byte)params);
        port.writeBytes(frame.getSupervisorFrameToWrite(),
                frame.getFrameSize());

    }

    public void getComPortParams(Frame frame){
        int params = frame.getData()[0];

        System.out.println(params);

        int parity = params & 1;
        int stopBits = (params & 6) >> 1;
        int bits = (params & 24) >> 3;
        int speed = (params & 224) >> 5;

        port.setComPortParameters(Baud.values()[speed].getSpeed(), DataBits.values()[bits].getBitsNum(), stopBits, parity);
        mainWindowUI.changeComPortParams(speed, bits, stopBits, parity);

    }

}
