package edu.ufl.itm.proj.algo;

import edu.ufl.itm.proj.BitMap;

import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hsitas444 on 10/7/2016.
 */
public class ProbabilisticCounting extends CardinalityEstimator{
    private int bitMapSize;
    private Map<InetAddress, BitMap> map;


    public ProbabilisticCounting(int bitMapSize,FileReader dataReader, FileReader cardinalityReader, FileWriter resultWriter) {
        super(dataReader, cardinalityReader, resultWriter);
        this.bitMapSize = bitMapSize;
        map = new HashMap<>();
    }

    @Override
    protected void preProcessOfflineOP() {}

    @Override
    protected void processOnlineData(InetAddress source, InetAddress destination) throws NoSuchAlgorithmException {
        BitMap bm = map.get(source);
        if(bm == null) {
            bm = new BitMap(bitMapSize);
            map.put(source, bm);
        }
        int destHash = getMD5Hash(destination);
        destHash = destHash % bitMapSize;
        if(destHash < 0)
            destHash += bitMapSize;
        bm.set(destHash);
    }

    @Override
    protected double estimateCardinality(InetAddress source) throws NoSuchAlgorithmException {
        int numBitsSet = 0;
        BitMap bitMap = map.get(source);
        for (int i = 0; i < bitMapSize; i++) {
            if (bitMap.isSet(i))
                numBitsSet++;
        }
        int numBitsUnset = bitMapSize - numBitsSet;
        if(numBitsUnset == 0)
            numBitsUnset = 1;
        double estimatedValue = - bitMapSize * Math.log(numBitsUnset / (double) bitMapSize);
        return estimatedValue;
    }
}
