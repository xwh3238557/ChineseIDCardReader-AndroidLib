package com.toonyoo.xiawenhao.cardreader;

import android.annotation.TargetApi;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSearchCard(){
        byte len1 = (byte)0xff;
        byte len2 = 0x01;
        int c = ((len1 <<8)|len2)& 0xffff;
        System.out.println(c);
    }
}