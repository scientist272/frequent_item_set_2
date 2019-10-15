package com.company;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Transfer {
    public static final BlockingDeque[] buffer = new BlockingDeque[4];
    public static final BlockingDeque<Map<Set<String>,Integer>> bufferForSecondPass = new LinkedBlockingDeque<>();
    static {
        for (int i = 0; i <4 ; i++) {
            buffer[i] = new LinkedBlockingDeque<Map<Set<String>,Integer>>();
        }
    }

}
