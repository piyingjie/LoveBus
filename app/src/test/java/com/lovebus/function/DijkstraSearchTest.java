package com.lovebus.function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

public class DijkstraSearchTest {
    private DijkstraSearch mDijkstraSearch;
    public DijkstraSearchTest(){
        System.out.println("construction method");
    }
    @Before
    public void setup(){
        if (mDijkstraSearch == null){
            mDijkstraSearch = new DijkstraSearch();
        }
    }
    @Test
    public void testPrint() {
        mDijkstraSearch.print();
    }
}