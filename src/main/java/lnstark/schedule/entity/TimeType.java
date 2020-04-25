package lnstark.schedule.entity;

public enum TimeType {

    Second(0, 0, 60, "second"),
    Minute(1, 0, 60, "minute"),
    Hour(2, 0, 23, "hour"),
    Day(3, 1, 31, "day"),
    Month(4, 0, 11, "month"),
    Week(5, 0, 7, "week"),
    Year(6, 0, Integer.MAX_VALUE, "year");

    int index;
    int max;
    int min;
    String name;

    TimeType(int index, int min, int max, String name) {
        this.index = index;
        this.min = min;
        this.max = max;
        this.name = name;
    }

}
