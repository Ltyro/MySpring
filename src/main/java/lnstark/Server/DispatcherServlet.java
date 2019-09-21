package lnstark.Server;

import lnstark.annotations.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DispatcherServlet extends HttpServlet {

    private void doDispatcher(HttpServletRequest req, HttpServletResponse res) {

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
