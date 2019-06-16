package configuration;

import annotations.Bean;
import annotations.Component;
import entity.Board;

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

    @Bean("board")
    public Board myBoard1() {
        return new Board(7, 4);
    }
}
