package configuration;

import entity.Board;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MyBeans {
    int i;

    public MyBeans() {
        i = 10;
    }

    @Bean
    public Board myBoard() {
        return new Board(5, 2);
    }

}
