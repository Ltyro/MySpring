package lnstark.schedule.entity;

import lnstark.exception.ScheduleException;
import lnstark.utils.StringUtil;

public class Time {

    private int value;

    private String expression;

    private boolean every;

    private TimeType timeType;

    private static final String STAR = "*";

    public Time(int value, boolean every, TimeType timeType) {
        this.value = value;
        this.every = every;
        this.timeType = timeType;
        validate();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        validate();
    }

    private void validate() {
        if (value > timeType.max)
            throw new ScheduleException(timeType.name + " should not more than " + timeType.max);
        if(value < timeType.min)
            throw new ScheduleException(timeType.name + " should not less than " + timeType.min);
    }

    public boolean isEvery() {
        return every;
    }

    public void setEvery(boolean every) {
        this.every = every;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {

        parseExpression(expression);

    }

    private void parseExpression(String expression) {
        if (StringUtil.isNumberOnly(expression)) {
            setValue(Integer.parseInt(expression));
        }

        if(STAR.equals(expression))
            every = true;

        this.expression = expression;
    }

}
