package Views;

public interface MainWindowUI {

    void changeLogicalConnectLabel();

    void changeComPortParams(int speed, int bits, int stopBits, int parity);

    void uiAfterDisconnect();

    String setUserName();

}
