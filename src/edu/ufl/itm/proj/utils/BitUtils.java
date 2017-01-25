package edu.ufl.itm.proj.utils;

/**
 * Created by hsitas444 on 10/8/2016.
 */
public class BitUtils {
    public static int intbitIdx[];
    public static long longbitIdx[];
    static{
        intbitIdx = new int[Integer.SIZE];
        intbitIdx[0] = 1;
        for(int i=1; i<Integer.SIZE;i++)
            intbitIdx[i] = intbitIdx[i-1] << 1;
        longbitIdx = new long[Long.SIZE];
        longbitIdx[0] = 1;
        for(int i=1; i<Integer.SIZE;i++)
            longbitIdx[i] = longbitIdx[i-1] << 1;
    }

    public static int[] split(int val, int index){
        int arr[] = new int[3];
        int mask = (int)(Math.pow(2.0, index) - 1);
        arr[0] = val & mask;
        arr[2] = val;
        arr[1] = val >>> index;
        return arr;
    }

    public static long countConsecutive0s(long data){
        int count=0;
        for(int i=0; i<Long.SIZE;i++){
            if((data & longbitIdx[i]) == 0)
                count++;
            else break;
        }
        return count;
    }

    public static int countConsecutive0s(int data){
        int count=0;
        for(int i=0; i<Integer.SIZE;i++){
            if((data & intbitIdx[i]) == 0)
                count++;
            else break;
        }
        return count;
    }

    public static void main(String args[]){
        int a[] = BitUtils.split(1034, 2);
        int b[] = BitUtils.split(1034, 3);
        int c[] = BitUtils.split(1034, 1);
        int d[] = BitUtils.split(1034, 0);
        int e[] = BitUtils.split(1034, 5);
        int f[] = BitUtils.split(1034, 4);
    }
}
