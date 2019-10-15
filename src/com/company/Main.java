package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here
        ArrayList[] buckets = FileOperator.readFile("itemlist10000.txt");
        FirstMapper firstMapper = new FirstMapper();
        FirstReducer firstReducer = new FirstReducer();
        SecondMapper secondMapper = new SecondMapper();
        SecondReducer secondReducer = new SecondReducer();

        firstMapper.map(buckets,100);
        secondMapper.map(buckets,firstReducer.reduce(),100);
        secondReducer.reduce();
    }
}
