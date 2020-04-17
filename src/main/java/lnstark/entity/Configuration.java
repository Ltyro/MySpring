package lnstark.entity;

public class Configuration {

    private int port = 8080;

    private String servletPath = "/defaultPath";

    private boolean enableSchedule = false;
    
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

	public boolean isEnableSchedule() {
		return enableSchedule;
	}

	public void setEnableSchedule(boolean enableSchedule) {
		this.enableSchedule = enableSchedule;
	}
}
