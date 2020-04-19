package lnstark.Controller;

import lnstark.annotations.Autowired;
import lnstark.annotations.Controller;
import lnstark.annotations.RequestMapping;
import lnstark.annotations.RequestParam;
import lnstark.aop.AopTestAnno;

import javax.servlet.http.HttpServletRequest;

@Controller("lbwnb")
@RequestMapping("cheer")
public class MyController {

    @Autowired
    Object a;

    @RequestMapping(value = {"pps/oos", "ps/os"})
    @AopTestAnno("hiahiahia")
    public String hello(HttpServletRequest request,
                        @RequestParam("s") String p1,
                        @RequestParam("d") int p2) {
        return "hello, flyer " + p1 + " " + p2;
    }

    @RequestMapping(value = {"pps/os"})
    public String helloGirl() {
        return "hello, traveler";
    }

}
