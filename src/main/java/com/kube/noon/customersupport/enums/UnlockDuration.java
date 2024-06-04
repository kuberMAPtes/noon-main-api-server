package com.kube.noon.customersupport.enums;

public enum UnlockDuration {
    ONE_DAY(1),
    THREE_DAYS(3),
    SEVEN_DAYS(7),
    THIRTY_DAYS(30),
    ONE_HUNDRED_EIGHTY_DAYS(180),
    THREE_HUNDRED_SIXTY_FIVE_DAYS(365),
    PERMANENT(Integer.MAX_VALUE);

    private final int days;

    UnlockDuration(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
