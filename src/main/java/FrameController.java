import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class FrameController {

    private SerialPort[] ports;
    private SerialPort port = null;

    public FrameController(){
        ports = SerialPort.getCommPorts();
    }

    public void setPort(int ind){
        this.port = ports[ind];

        //открытие COM-порта и установка сигнала DTR
        port.openPort();
        port.setDTR();
    }

    public SerialPort[] getPorts(){
        return ports;
    }

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
    }

    public void setComPortParams(){
        //port.setComPortParameters((int)comboBoxSpeed.getSelectedItem(), (int)comboBoxBits.getSelectedItem(), comboBoxStopBits.getSelectedIndex(), comboBoxParity.getSelectedIndex());
    }

    private void processFrame(byte[] data){
        Frame frame = new Frame(data);
        switch(frame.getType()){
            case LINK:
                System.out.println("LINK");
                Frame connectionFrame = new Frame(FrameTypes.LINK);
                port.writeBytes(connectionFrame.getSupervisorFrameToWrite(),
                        connectionFrame.getFrameSize());
                break;
            case ACK:
                System.out.println("ACK");
                break;
        }
    }

}
