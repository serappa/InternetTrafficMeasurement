package edu.ufl.itm.proj.algo;

import edu.ufl.itm.proj.BitMap;

import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by hsitas444 on 10/7/2016.
 */
public class SampledVirtualBitmap extends VirtualBitmap {


    protected double samplingFactor;
    public SampledVirtualBitmap(double samplingFactor, int vMapSize, int realBMapSize, FileReader dataReader, FileReader cardinalityReader, FileWriter resultWriter) {
        super(vMapSize, realBMapSize, dataReader, cardinalityReader, resultWriter);
        this.samplingFactor = samplingFactor;
    }

    @Override
    protected double estimateCardinality(InetAddress source) throws NoSuchAlgorithmException {
        double val = super.estimateCardinality(source);
        return (val /samplingFactor);
    }

    @Override
    protected void processOnlineData(InetAddress source, InetAddress destination) throws NoSuchAlgorithmException {
        if(Math.random() <= samplingFactor)
            super.processOnlineData(source, destination);
    }

}
