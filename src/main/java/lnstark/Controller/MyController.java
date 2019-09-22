package lnstark.Controller;

import lnstark.annotations.Autowired;
import lnstark.annotations.Controller;
import lnstark.annotations.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller("lbwnb")
public class MyController {

    @Autowired
    Object a;

    @RequestMapping(value = {"pps/oos", "ps/os"})
    public String hello(HttpServletRequest request, String p1, int p2) {
        return "hello, flyer " + p1 + " " + p2;
    }

}
