package lnstark.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController("hi")
@RequestMapping("hhh")
public class HelloController {

    @RequestMapping({"hello", "he////df"})
    public String hello(HttpServletRequest request, String p1) {
        System.out.println(request.getRemoteAddr());
        System.out.println(p1);
        return "hello, your grace";
    }

}
