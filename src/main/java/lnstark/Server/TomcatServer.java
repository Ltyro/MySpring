package lnstark.Server;

import lnstark.annotations.Component;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Component
public class TomcatServer {

    public String WEBAPP_PATH = "src/main";

    public static void main(String args[]) {
        new TomcatServer().startService();
    }

    public void startService() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        String tmpDirPath = System.getProperty("user.dir") + File.separator + WEBAPP_PATH;
        org.apache.catalina.Context ctxt = tomcat.addContext("/sqrt", tmpDirPath);

        Tomcat.addServlet(ctxt, "servletTest", new HttpServlet() {

            protected void doGet(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {

                Double input = Double.parseDouble(request.getParameter("number"));
                double result = Math.sqrt(input);
                String message = "The result is " + result;
                response.getOutputStream().write(message.getBytes());

            }
        });

        ctxt.addServletMappingDecoded("/", "servletTest");
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        tomcat.getServer().await();
    }

}
