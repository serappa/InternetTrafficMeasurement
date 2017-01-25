package edu.ufl.itm.proj.algo;

import edu.ufl.itm.proj.BlockBitMaps;
import edu.ufl.itm.proj.utils.BitUtils;
import edu.ufl.itm.proj.utils.ByteUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hsitas444 on 10/8/2016.
 */
public class HyperLogLog extends CardinalityEstimator {
    private static int intbitIdx[];
    static{
        intbitIdx = new int[Integer.SIZE];
        intbitIdx[0] = 1;
        for(int i=1; i<Integer.SIZE;i++)
            intbitIdx[i] = intbitIdx[i-1] << 1;
    }
    private int counterSize;
    private int numSketches;
    private int numSketchesPow2;
    private double twoPow32;
    private double alpha;
    private Map<InetAddress, BlockBitMaps> map;
    private int b;
    public HyperLogLog(int counterSize, int numSketches, FileReader dataReader, FileReader cardinalityReader, FileWriter resultWriter) {
        super(dataReader, cardinalityReader, resultWriter);
        this.counterSize = counterSize;
        map = new HashMap<>();
        this.numSketches = numSketches;
        b = (int) (Math.ceil(Math.log(numSketches)/Math.log(2)));
        alpha = getAlpha(numSketches);
        numSketchesPow2 = numSketches * numSketches;
        twoPow32 = Math.pow(2.0, 32);
    }

    @Override
    protected void processOnlineData(InetAddress source, InetAddress destination) throws NoSuchAlgorithmException {
        BlockBitMaps bitMaps = map.get(source);
        if(bitMaps == null){
            bitMaps = new BlockBitMaps(numSketches, counterSize);
            map.put(source, bitMaps);
        }
        byte hashBytes[] = getBigHash(destination,4);
        int hash = ByteUtils.bytesToInt(hashBytes);
        int split[] = BitUtils.split(hash, b);
        int p = split[0];
        p = p % numSketches;
        p = p < 0? p+numSketches : p;
        int q =  BitUtils.countConsecutive0s(split[1])+1;

        bitMaps.setBlockIntValue(p, Math.max(bitMaps.blockIntValue(p),q));




    }

    private double getAlpha(int numSketches){
        if(numSketches == 16)
            return 0.673;
        else if(numSketches == 32)
            return 0.697;
        else if(numSketches == 64)
            return 0.709;
        else {
            //for numSketches >=128
            return 0.7213 /(1+ 1.079/numSketches);
        }
    }

    @Override
    protected void preProcessOfflineOP() {

    }

    @Override
    protected double estimateCardinality(InetAddress source) throws NoSuchAlgorithmException {
        BlockBitMaps bitMaps = map.get(source);
        double sum = 0.0;
        int v = 0; // Number of values equal to zero
        for(int i=0;i<numSketches;i++){
            int val = bitMaps.blockIntValue(i);
            sum += 1/Math.pow(2.0, val);
            if(val == 0)
                v++;
        }
        double estimatedValue = alpha * numSketchesPow2 / sum ;

        //estimatedValue corrections mentioned in hyperloglog paper
        if(estimatedValue <= 2.5 * numSketches ){
            if(v!=0){
                estimatedValue = numSketches * Math.log(numSketches/(double)v);
            }
        } else if(estimatedValue > twoPow32/30){
            estimatedValue = - twoPow32 * (1 - estimatedValue/twoPow32);
        }
        return estimatedValue;
    }
}
