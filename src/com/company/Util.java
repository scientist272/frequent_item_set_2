package com.company;

import java.util.Set;

public class Util {
    public static String setToString (Set<String> items){
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (String item:items){
            builder.append(item).append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append("}");
        return builder.toString();
    }
}
