package edu.ufl.itm.proj.algo;

import edu.ufl.itm.proj.BitMap;
import edu.ufl.itm.proj.utils.ByteUtils;
import edu.ufl.itm.proj.utils.HashUtils;

import java.io.*;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by hsitas444 on 10/7/2016.
 */
public class VirtualBitmap extends CardinalityEstimator {

    protected int vMapSize;
    protected int realBMapSize;
    protected BitMap bitMap;
    protected List<Integer> rand;

    protected double noise;

    public VirtualBitmap(int vMapSize, int realBMapSize, FileReader dataReader, FileReader cardinalityReader, FileWriter resultWriter) {
        super(dataReader,cardinalityReader,resultWriter);
        this.vMapSize = vMapSize;
        this.realBMapSize = realBMapSize;
        this.bitMap = new BitMap(realBMapSize);
        rand = new ArrayList<>();
        Random randGen = new Random();
        for (int i = 0; i < vMapSize; i++)
            rand.add(randGen.nextInt());
        Collections.shuffle(rand);

    }

    @Override
    protected double estimateCardinality(InetAddress source) throws NoSuchAlgorithmException {
        int numBitsSet = 0;
        int sourceHashCode = getFastHash(source);
        for (int i = 0; i < vMapSize; i++) {
            int bitToGet = sourceHashCode ^ rand.get(i);
            bitToGet = getFastHash(bitToGet);
            bitToGet = bitToGet % realBMapSize;
            if (bitToGet < 0)
                bitToGet += realBMapSize;
            if (bitMap.isSet(bitToGet))
                numBitsSet++;
        }
        int numBitsUnset = vMapSize - numBitsSet;
        if(numBitsUnset == 0)
            numBitsUnset = 1;
        double estimatedValue = noise - vMapSize * Math.log(numBitsUnset / (double) vMapSize);
        if (estimatedValue < 0) {
            estimatedValue = 0;
        }
        return estimatedValue;
    }

    @Override
    protected void processOnlineData(InetAddress source, InetAddress destination) throws NoSuchAlgorithmException {
        int destHash = getFastHash(destination);
        int sourceHash = getFastHash(source);
        destHash = destHash % vMapSize;
        if (destHash < 0)
            destHash += vMapSize;
        int bitToSet = sourceHash ^ rand.get(destHash);
        bitToSet = getFastHash(bitToSet);
        bitToSet = bitToSet % realBMapSize;
        if (bitToSet < 0)
            bitToSet += realBMapSize;
        bitMap.set(bitToSet);
    }

    @Override
    protected void preProcessOfflineOP() {
        int numBitsUnset = bitMap.bitsUnset();
        noise = vMapSize * Math.log(numBitsUnset/(double)realBMapSize);
    }

}
