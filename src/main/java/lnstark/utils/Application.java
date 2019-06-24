package lnstark.utils;

import lnstark.App;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;


public class Application {

    private static Log log = LogFactory.getLog(Application.class);

    private String path = "", packageName = "";

    private Class clz = null;
    private Application(Class<?> clz) {
        path = clz.getResource("").getFile();
        path = path.replaceAll("%20"," ");// 替换空格

        packageName = clz.getPackage().getName();
        this.clz = clz;
    }

    public Context run() {
        Scanner.getInstance().scanBeans(path, packageName);

        log.info("--------- " + clz.getSimpleName() + " started -------------");
        return Context.getInstance();
    }

    private void startServer() {


    }

    public static Context run(Class<?> clz) {

        return new Application(clz).run();

    }


}
