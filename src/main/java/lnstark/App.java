package lnstark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lnstark.utils.Scanner;

import static java.lang.System.out;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
//        run();
        SpringApplication.run(App.class);
    }

    static void run() {
        String path = App.class.getResource("").getFile();
        path = path.replaceAll("%20"," ");
        Scanner.getInstance().scanBeans(path);

        out.println("hello");
    }
}
