package lnstark.entity;

public class Configuration {

    private int port = 8080;

    private String servletPath = "/defaultPath";

    public Configuration() {
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }
}
