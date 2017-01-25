package edu.ufl.itm.proj.utils;

/**
 * Created by hsitas444 on 10/12/2016.
 */

//Using the fast hash method described in http://burtleburtle.net/bob/hash/integer.html

public class HashUtils {

    public static int fastHash(int val){
        val = (val ^ 61) ^ (val >> 16);
        val = val + (val << 3);
        val = val ^ (val >> 4);
        val = val * 0x27d4eb2d;
        val = val ^ (val >> 15);
        return val;
    }


}
