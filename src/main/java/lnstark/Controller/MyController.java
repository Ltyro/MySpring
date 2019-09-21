package lnstark.Controller;

import lnstark.annotations.Autowired;
import lnstark.annotations.Controller;
import lnstark.annotations.RequestMapping;

@Controller("lbwnb")
public class MyController {

    @Autowired
    Object a;

    @RequestMapping({"pps/oos", "ps/os"})
    public String hello() {
        return "hello, flyer";
    }

}
