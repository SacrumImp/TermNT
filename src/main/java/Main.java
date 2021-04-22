
import java.awt.*;

import Views.MainWindow;

public class Main {

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setTitle("Окно настройки");
        mainWindow.pack();
        mainWindow.setSize(new Dimension(500, 400));
        mainWindow.setVisible(true);
    }



}
