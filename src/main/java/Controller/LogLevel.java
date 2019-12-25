package Controller;

public enum LogLevel {
    INFO("INFO"),
    DEBUG("DEBUG"),
    LOG("LOG");


    private String level;

    LogLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
}
