package lnstark.utils;

import lnstark.Server.TomcatServer;
import lnstark.entity.Configuration;
import lnstark.utils.context.Context;
import lnstark.utils.context.ContextAware;
import lnstark.utils.context.DefaultContext;
import lnstark.utils.context.WebContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Application {

    private static Log log = LogFactory.getLog(Application.class);

    private String path = "", packageName = "";

    private Context context = null;

    private Class clz = null;

    private Configuration config;

    private Application(Class<?> clz) {
        // 扫描包的路径
        path = clz.getResource("").getFile();
        path = path.replaceAll("%20", " ");// 替换空格
        config = new ConfigurationResolver();

        packageName = clz.getPackage().getName();
        this.clz = clz;

        context = new WebContext();
        ContextAware.setContext(context);
    }

    public Context run() {

        Scanner.getInstance().scanBeans(path, packageName);
        startServer();
        log.info("--------- " + clz.getSimpleName() + " started -------------");

        return context;
    }

    private void startServer() {

        TomcatServer server = (TomcatServer) context.getBeanByName("tomcatServer");
        server.setContextPath(config.getServletPath());
        server.setPort(config.getPort());
        server.start();

    }

    public static Context run(Class<?> clz) {

        return new Application(clz).run();

    }


}
