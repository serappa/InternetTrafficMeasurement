package edu.ufl.itm.proj.algo;

import edu.ufl.itm.proj.BitMap;
import edu.ufl.itm.proj.utils.BitUtils;
import edu.ufl.itm.proj.utils.ByteUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hsitas444 on 10/7/2016.
 */
public class FMSketch extends CardinalityEstimator {
    private int counterSize;
    private int numSketches;
    private Map<InetAddress, BitMap[]> sketch;
    public FMSketch(int counterSize,int numSketches, FileReader dataReader, FileReader cardinalityReader, FileWriter resultWriter) {
        super(dataReader, cardinalityReader, resultWriter);
        this.counterSize = counterSize;
        sketch = new HashMap<>();
        this.numSketches = numSketches;
    }

    @Override
    protected void processOnlineData(InetAddress source, InetAddress destination) throws NoSuchAlgorithmException {
        BitMap bitMaps[] = sketch.get(source);
        if(bitMaps == null){
            bitMaps = new BitMap[numSketches];
            for(int i=0; i< bitMaps.length; i++)
                bitMaps[i] = new BitMap(counterSize);
            sketch.put(source, bitMaps);
        }


        byte hashBytes[] = getBigHash(destination, 4*numSketches );
        for(int i=0; i< numSketches; i++) {
            int consecute0s = BitUtils.countConsecutive0s(ByteUtils.bytesToInt(hashBytes,4*i));
            bitMaps[i].set(consecute0s);
        }

    }


    @Override
    protected void preProcessOfflineOP() {

    }

    @Override
    protected double estimateCardinality(InetAddress source) throws NoSuchAlgorithmException {
        BitMap[] bitMaps = sketch.get(source);
        double avgConsecBitsSet = 0;
        for(BitMap bitMap : bitMaps)
            avgConsecBitsSet+= bitMap.getConsecutiveSetBits();
        avgConsecBitsSet = avgConsecBitsSet /numSketches;
        double cardinality = Math.pow(2.0,avgConsecBitsSet);
        return cardinality;
    }
}
