package Controller;

public enum LogType {
    CONSOLE("CONSOLE"),
    FILE("FILE"),
    ALL("ALL"),
    NONE("NONE");

    private String type;

    LogType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
