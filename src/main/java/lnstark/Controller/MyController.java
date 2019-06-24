package lnstark.Controller;

import lnstark.annotations.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyController {

    @Autowired
    Object a;

    @RequestMapping
    public String hello() {
        return "hello, flyer";
    }

}
