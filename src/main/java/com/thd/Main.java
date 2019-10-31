package com.thd;

import java.util.Calendar;
import java.util.Random;

public class Main {


    private static final Random RANDOM = new Random();

    private static final long MAGIC = 18116;

    public static void main(String[] args) {

        long days = Calendar.getInstance().getTimeInMillis() / (1000*3600*24);

        for (int i = 0; i < 60; i++) {
            long day = days + i;

            System.out.println("Day: " + (day-MAGIC));
            System.out.println("Day: " + (day-MAGIC)*300);

        }


    }
}
