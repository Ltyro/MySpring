package lnstark.Server;

import lnstark.annotations.Component;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component("tomcatServer")
public class TomcatServer {

    public String WEBAPP_PATH = "src/main";

    private int port = 8080;

    private String contextPath = "/defaultPath";

    public void start() {
        new Thread(() -> {
            runService();
        }).start();
    }

    public static void main(String[] args) {
//        System.out.println("h");
        new TomcatServer().runService();
    }

    public void runService() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        String tmpDirPath = System.getProperty("user.dir") + File.separator + WEBAPP_PATH;
        org.apache.catalina.Context ctxt = tomcat.addContext(contextPath, tmpDirPath);

//        Tomcat.addServlet(ctxt, "servlet", new HttpServlet() {
//
//            protected void doGet(HttpServletRequest request,
//                                 HttpServletResponse response) throws IOException {
//
//                Double input = Double.parseDouble(request.getParameter("number"));
//                double result = Math.sqrt(input);
//                String message = "The result is " + result;
//                response.getOutputStream().write(message.getBytes());
//
//            }
//
//        });

        Tomcat.addServlet(ctxt, "servlet", new DispatcherServlet());

        ctxt.addServletMappingDecoded("/", "servlet");
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        tomcat.getServer().await();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
