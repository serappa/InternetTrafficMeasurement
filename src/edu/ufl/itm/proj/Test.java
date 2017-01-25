package edu.ufl.itm.proj;

import edu.ufl.itm.proj.utils.ByteUtils;

/**
 * Created by hsitas444 on 12/7/2016.
 */
public class Test {
    public static void main(String args[]){
        int i = 162;
        byte[] b = ByteUtils.intToBytes(i);
        int a = ByteUtils.bytesToInt(b);
    }



}
