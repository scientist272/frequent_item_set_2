package com.company;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileOperator {
    public static ArrayList[] readFile(String path){
        ArrayList[] buckets = new ArrayList[10000];
        for (int i = 0; i < buckets.length ; i++) {
            buckets[i] = new ArrayList<String>();
        }
        try(InputStreamReader read = new InputStreamReader(new FileInputStream(path));
            BufferedReader reader = new BufferedReader(read)){
            int index = 0;
            while(true){
                String line = reader.readLine();
                if(index==buckets.length || line==null)
                    break;
                String[] bucket = line.split(" ");
                for (int i = 0; i <bucket.length ; i++) {
                    buckets[index].add(bucket[i]);
                }
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buckets;
    }
}
