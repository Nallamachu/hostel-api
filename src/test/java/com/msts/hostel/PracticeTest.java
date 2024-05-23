package com.msts.hostel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class PracticeTest {
    public static void main(String[] args) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        String startDate = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).format(format);
        String endDate = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(format);
        String current_month_startDate = now.with(TemporalAdjusters.firstDayOfMonth()).format(format);
        System.out.println(current_month_startDate);
        System.out.println(now.format(format));
        System.out.println(startDate);
        System.out.println(endDate);
    }
}
