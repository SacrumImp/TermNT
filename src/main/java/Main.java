
import java.awt.*;

import com.fazecast.jSerialComm.*;

public class Main {

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.pack();
        mainWindow.setSize(new Dimension(500, 400));
        mainWindow.setVisible(true);

        SerialPort[] ports = SerialPort.getCommPorts();
        mainWindow.setPortNames(ports);
    }



}
