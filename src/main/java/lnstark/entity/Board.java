package lnstark.entity;


public class Board {

    int wheel = 4;

    int plank = 1;

    public Board() {

    }

    public Board(int wheel, int plank) {
        this.wheel = wheel;
        this.plank = plank;
    }

    public int getWheel() {
        return wheel;
    }

    public void setWheel(int wheel) {
        this.wheel = wheel;
    }

    public int getPlank() {
        return plank;
    }

    public void setPlank(int plank) {
        this.plank = plank;
    }
}
