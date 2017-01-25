package edu.ufl.itm.proj.utils;

/**
 * Created by hsitas444 on 10/8/2016.
 */
public class ByteUtils {
    public static int bytesToInt(byte bytes[]){
        int value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        return value;
    }

    public static int bytesToInt(byte bytes[], int offset){
        int value = ((bytes[offset] & 0xFF) << 24) | ((bytes[offset+1] & 0xFF) << 16)
                | ((bytes[offset+2] & 0xFF) << 8) | (bytes[offset+3] & 0xFF);
        return value;
    }

    public static byte[] intToBytes(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value };
    }


}
