package lnstark.utils;

import lnstark.App;
import lnstark.Server.TomcatServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;


public class Application {

    private static Log log = LogFactory.getLog(Application.class);

    private String path = "", packageName = "";

    private Context context = null;

    private Class clz = null;
    private Application(Class<?> clz) {
        path = clz.getResource("").getFile();
        path = path.replaceAll("%20"," ");// 替换空格

        packageName = clz.getPackage().getName();
        this.clz = clz;

        context = Context.getInstance();
    }

    public Context run() {

        Scanner.getInstance().scanBeans(path, packageName);
        startServer();
        log.info("--------- " + clz.getSimpleName() + " started -------------");

        return context;
    }

    private void startServer() {

        TomcatServer server = (TomcatServer) context.getBeanByName("tomcatServer");
        server.start();

    }

    public static Context run(Class<?> clz) {

        return new Application(clz).run();

    }


}
