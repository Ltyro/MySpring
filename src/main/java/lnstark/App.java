package lnstark;

import lnstark.entity.Board;
import lnstark.utils.Application;
import lnstark.utils.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lnstark.utils.Scanner;

import java.util.List;

import static java.lang.System.out;

@SpringBootApplication
public class App {

    private static Log log = LogFactory.getLog(App.class);

    public static void main(String[] args) {
        Context ctx = Application.run(App.class);
        Board b1 = (Board) ctx.getBeanByName("myBoard");
        Board b2 = (Board) ctx.getBeanByName("board");
        List<Board> boards = ctx.getBeanByType(Board.class);
        log.info("hello");
//        SpringApplication.run(App.class);
    }

//    static void run() {
//        String path = App.class.getResource("").getFile();
//        path = path.replaceAll("%20"," ");
//        Scanner.getInstance().scanBeans(path);
//
//        out.println("hello");
//    }
}
