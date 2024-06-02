package com.inaxdevelopers.monitorbatterystatus.batteryusage.util;


public enum SortEnum {
    TODAY(0),
    YESTERDAY(1),
    THIS_WEEK(2),
    THIS_MONTH(3),
    THIS_YEAR(4);
    
    int sort;

    SortEnum(int i) {
        this.sort = i;
    }

    public static SortEnum getSortEnum(int i) {
        if (i == 0) {
            return TODAY;
        }
        if (i == 1) {
            return YESTERDAY;
        }
        if (i == 2) {
            return THIS_WEEK;
        }
        if (i == 3) {
            return THIS_MONTH;
        }
        if (i != 4) {
            return TODAY;
        }
        return THIS_YEAR;
    }
}
