package edu.ufl.itm.proj;

import edu.ufl.itm.proj.utils.BitUtils;

/**
 * Created by hsitas444 on 10/10/2016.
 */
public class BlockBitMaps {
    //This class is not very generic only works for each bitMap size < 32
    private BitMap map;
    private final int numBlocks;
    private final int eachBitMapSize;

    private final int MAX_INT;
    public BlockBitMaps(int numBlocks, int eachBitMapSize){
        this.numBlocks = numBlocks;
        this.eachBitMapSize = eachBitMapSize;
        map = new BitMap(numBlocks * eachBitMapSize);
        if(eachBitMapSize>=32)
            throw new IllegalArgumentException("Only supports a max of 31 for eachBitMapSize");
        this.MAX_INT = (int) Math.pow(2.0, eachBitMapSize) - 1 ;
    }

    public int numBlocks(){
        return numBlocks;
    }

    private void validateRange(int blockNum,  int bitNum) {
        if (blockNum >= numBlocks || bitNum <0 || bitNum >= eachBitMapSize || bitNum<0)
            throw new IllegalArgumentException("Invalid bitNum: " + bitNum + " or invalid blockNum: "+blockNum);
    }

    public void set(int blockNum, int bitNum){
        validateRange(blockNum, bitNum);
        map.set(blockNum*eachBitMapSize + bitNum);
    }

    public boolean isSet(int blockNum, int bitNum){
        validateRange(blockNum, bitNum);
        return map.isSet(blockNum*eachBitMapSize + bitNum);
    }

    public int blockIntValue(int blockNum){
        double val = 0;
        for(int i=0;i<eachBitMapSize;i++){
            if(map.isSet(blockNum*eachBitMapSize + i))
                val += Math.pow(2.0,i);
        }
        return (int)val;
    }

    public void setBlockIntValue(int blockNum, int val){
        if(blockNum < 0 || blockNum >= numBlocks)
            throw new IllegalArgumentException("Invalid blockNum: "+ blockNum);
        if(val < 0 || val > MAX_INT ) // May not need for the project, remove if it become expensive
            throw new IllegalArgumentException("Invalid int: "+ val);
        for(int i=0;i<eachBitMapSize;i++){
            if((val & BitUtils.intbitIdx[i]) != 0)
                map.set(blockNum*eachBitMapSize+i);
            else map.clear(blockNum*eachBitMapSize+i);

        }
    }
}
