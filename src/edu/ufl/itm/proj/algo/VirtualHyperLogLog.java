package edu.ufl.itm.proj.algo;

import edu.ufl.itm.proj.BlockBitMaps;
import edu.ufl.itm.proj.utils.BitUtils;
import edu.ufl.itm.proj.utils.ByteUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

/**
 * Created by hsitas444 on 10/8/2016.
 */
public class VirtualHyperLogLog extends CardinalityEstimator {
    private static int intbitIdx[];
    static{
        intbitIdx = new int[Integer.SIZE];
        intbitIdx[0] = 1;
        for(int i=1; i<Integer.SIZE;i++)
            intbitIdx[i] = intbitIdx[i-1] << 1;
    }
    protected int counterSize;
    protected int numSketches;
    protected int totalSketches;
    protected BlockBitMaps bitMaps;
    private int numSketchesPow2;
    private double twoPow32;
    private double alpha;
    private int b;
    private double nCap;
    private int rand[];

    public VirtualHyperLogLog(int counterSize, int numSketchesPerFlow, int totalSketches,
                              FileReader dataReader, FileReader cardinalityReader, FileWriter resultWriter) {
        super(dataReader, cardinalityReader, resultWriter);


        this.counterSize = counterSize;
        this.totalSketches = totalSketches;
        this.numSketches = numSketchesPerFlow;
        preComputeVals();
        bitMaps = new BlockBitMaps(this.totalSketches,counterSize);

    }

    private void preComputeVals(){
        b = (int) (Math.ceil(Math.log(numSketches)/Math.log(2)));
        alpha = getAlpha(numSketches);
        numSketchesPow2 = numSketches * numSketches;
        twoPow32 = Math.pow(2.0, 32);
        rand = new int[numSketches];
        Random r = new Random();
        for(int i=0;i<numSketches; i++){
            rand[i] = r.nextInt();
        }
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
    protected void processOnlineData(InetAddress source, InetAddress destination) throws NoSuchAlgorithmException {
        int destHash = getMurMurHash3(destination);
        int split[] = BitUtils.split(destHash, b);
        int p = split[0];
        p = p % numSketches;
        p = p<0 ? p+numSketches : p;
        int q = BitUtils.countConsecutive0s(split[1]) + 1;
        int fp = getMurMurHash3(ByteUtils.intToBytes(getMurMurHash3(source) ^ rand[p]));
        fp = fp % totalSketches;
        fp = fp < 0? fp + totalSketches : fp;
        bitMaps.setBlockIntValue(fp,Math.max(bitMaps.blockIntValue(fp),q));
        int test = bitMaps.blockIntValue(fp);
        assert(test == q);
    }

    @Override
    protected void preProcessOfflineOP() {
        nCap = computeNCap();
    }

    @Override
    protected double estimateCardinality(InetAddress source) throws NoSuchAlgorithmException {
        double ns = computeNs(source);
        return ns;
    }

    private double computeNs(InetAddress source)throws NoSuchAlgorithmException{
        double sum = 0.0;
        int v = 0; // Number of zero valued sketches
        for(int k=0;k<numSketches;k++){
            int fp = getMurMurHash3(ByteUtils.intToBytes(getMurMurHash3(source) ^ rand[k]));
            fp = fp % totalSketches;
            fp = fp < 0? fp + totalSketches : fp;
            int val = bitMaps.blockIntValue(fp);
            if(val == 0)
                v++;
            sum += Math.pow(2.0, -val);
        }
        double ns = alpha * numSketchesPow2 / sum ;
        ns = performCorrections(ns, v, numSketches);
        double nf;
        double m = totalSketches;
        double s = numSketches;
        nf = (m*s /(m-s)) * Math.abs(ns/s - nCap/m);
        return (int) nf;

    }


    public double computeNCap(){
        double nCap = 0.0;
        double sum = 0.0;
        int v = 0; // Number of values equal to zero
        for(int i=0;i<totalSketches;i++){
            int val = bitMaps.blockIntValue(i);
            sum += Math.pow(2.0, -val);
            if(val == 0)
                v++;
        }
        nCap = getAlpha(totalSketches) * (double)totalSketches * (double) totalSketches / sum ;
        nCap = performCorrections(nCap, v, totalSketches);
        return nCap;
    }

    public double performCorrections(double estimatedValue,  int v, int nSketches){
        //estimatedValue corrections mentioned in hyperloglog paper
        if(estimatedValue <= 2.5 * nSketches ){
            if(v!=0){
                estimatedValue = nSketches * Math.log(nSketches/(double)v);
            }
        } else if(estimatedValue > twoPow32/30){
            estimatedValue = - twoPow32 * (1 - estimatedValue/twoPow32);
        }
        return estimatedValue;
    }
}
