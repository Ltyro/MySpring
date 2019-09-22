package lnstark.Server;

import lnstark.annotations.Component;
import lnstark.entity.RequestHandler;
import lnstark.exception.RequestMappingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class DispatcherServlet extends HttpServlet {

    private Log log = LogFactory.getLog(DispatcherServlet.class);

    private void doDispatcher(HttpServletRequest req, HttpServletResponse res) {
        PrintWriter writer = null;
        try {

            res.setContentType("text/html;charset=utf-8");
            writer = res.getWriter();   // 这步要在setContentType之后，否则无效
            RequestHandler handler = MethodMappingResolver.getInstance().getHandler(req);
            if(handler == null)
                log.error("找不到相应的处理方法");
            String msg = handler.handle(req);
            writer.write(msg);
        } catch (RequestMappingException | IOException e) {
            e.printStackTrace();
            writer.write(e.getMessage());
        }
    }




    public void doService(HttpServletRequest req, HttpServletResponse res) {
        doDispatcher(req, res);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        doService(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doService(req, resp);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        doService(req, resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doService(req, resp);
    }
}
