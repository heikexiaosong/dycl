package com.thd.utils;

public class URLParser {
    public static String getParam(String code, String paramName) {
        String result = "";
        if ( code==null || code.trim().length()==0
                || paramName==null || paramName.trim().length()==0 ) {
            return result;
        }

        if ( code.contains("?") ){
            String query = code.substring(code.indexOf("?")+1);
            String[] params = query.split("&");
            if ( params!=null && params.length > 0 ){
                for (String param : params) {
                    String[] values = param.split("=");
                    if ( values!=null && values.length==2 ){
                        if ( paramName.trim().equalsIgnoreCase(values[0]) ){
                            return values[1];
                        }
                    }
                }
            }
        }
        return result;
    }
}
