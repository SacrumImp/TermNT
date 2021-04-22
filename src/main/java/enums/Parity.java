package enums;

public enum Parity {

    A ("Нет"),
    B ("Да");

    private final String status;

    Parity(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
