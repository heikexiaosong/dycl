package com.thd;

import java.util.Random;

public class Main {


    private static final Random RANDOM = new Random();

    private static final long MAGIC = 18116;

    public static void main(String[] args) {

        String code = "http://sssd.com/dfdf.asp?ss=1&itemcode=1234&sf=0";
         if ( code.contains("?") ){
             String query = code.substring(code.indexOf("?")+1);

             String[] params = query.split("&");
             for (String param : params) {
                 System.out.println(param);
                 String[] values = param.split("=");

                 System.out.println(values[1]);
             }
         }


    }
}
