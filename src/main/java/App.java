import configuration.MyBeans;
import utils.Context;
import utils.Scanner;

import java.util.List;

import static java.lang.System.out;
public class App {
    public static void main(String[] args) {
        String path = App.class.getResource("").getFile();
        path = path.replaceAll("%20"," ");
        Scanner.getInstance().scanBeans(path);

        out.println("hello");

    }
}
