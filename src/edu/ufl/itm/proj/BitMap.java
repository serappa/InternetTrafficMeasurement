package edu.ufl.itm.proj;

/**
 * Created by hsitas444 on 9/7/2016.
 */
public class BitMap {

    protected int size;
    protected long[] bitMap;
    protected static long[] bitIdx;

    public BitMap(int size) {
        this.size = size;
        int bitMapSize = (int) Math.ceil(size / 64.0);
        bitMap = new long[bitMapSize];
        if(bitIdx == null){
            bitIdx = new long[Long.SIZE];
            bitIdx[0] = 1;
            for (int i = 1; i < Long.SIZE; i++)
                bitIdx[i] = bitIdx[i - 1] << 1;
        }
    }

    private void validateRange(int bitNum) {
        if (bitNum >= bitMap.length * Long.SIZE || bitNum < 0 || bitNum >= size)
            throw new IllegalArgumentException("Invalid bitNum: " + bitNum);
    }

    public boolean isSet(int bitNum) {
        validateRange(bitNum);
        int arrIdx = bitNum / Long.SIZE;
        return (bitMap[arrIdx] & bitIdx[bitNum - Long.SIZE * arrIdx]) != 0;
    }


    public void set(int bitNum) {
        validateRange(bitNum);
        int arrIdx = bitNum / Long.SIZE;
        bitMap[arrIdx] = (bitMap[arrIdx] | bitIdx[bitNum - Long.SIZE * arrIdx]);
    }

    public void clear(int bitNum) {
        validateRange(bitNum);
        int arrIdx = bitNum / Long.SIZE;
        bitMap[arrIdx] = (bitMap[arrIdx] & ~bitIdx[bitNum - Long.SIZE * arrIdx]);
    }

    public void clearAll() {
        for (int i = 0; i < bitMap.length; i++)
            bitMap[i] = 0L;
    }

    public void setAll() {
        for (int i = 0; i < bitMap.length; i++)
            bitMap[i] = ~0L;
    }

    public int bitsSet(){
        int numSet =0;
        for(int i=0; i < size ; i++)
            if(isSet(i))
                numSet++;
        return numSet;
    }

    public int getConsecutiveSetBits(){
        int count =0;
        for(int i=0; i<size;i++){
            if(isSet(i))
                count++;
            else break;
        }
        return count;
    }

    public int getConsecutiveUnSetBits(){
        int count =0;
        for(int i=0; i<size;i++){
            if(!isSet(i))
                count++;
            else break;
        }
        return count;
    }

    public int size() {
        return size;
    }

    public int bitsUnset(){
        return size - bitsSet();
    }
}