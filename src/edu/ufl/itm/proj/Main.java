package edu.ufl.itm.proj;

import edu.ufl.itm.proj.algo.*;
import edu.ufl.itm.proj.utils.BitUtils;
import edu.ufl.itm.proj.utils.ByteUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        File cFile = new File("cardinality.csv");
        File rFile = new File("reduced.csv");
        if(!cFile.exists() || !rFile.exists()){
            PacketTraceScanner scanner = new TAFormatScanner(0,1);
            String traceFile = "D:\\AtUF\\Internet Traffic measurement\\equinix-chicago.dirA.20150219-125911.UTC.anon.pcap\\FlowTraffic.txt";
//            PacketTraceScanner scanner = new CSVPacketTraceScanner(2,3);
//            String traceFile = "D:\\AtUF\\Internet Traffic measurement\\equinix-chicago.dirA.20150219-125911.UTC.anon.pcap\\packets.csv";

            scanner.scan(traceFile);
        }

        try(FileReader cr = new FileReader(cFile); FileReader rr = new FileReader(rFile); FileWriter result= new FileWriter("result.csv")){
            CardinalityEstimator ce = null;
            //ce = new ProbabilisticCounting(512, rr, cr, result);
            //ce = new VirtualBitmap(512, 1 * 1024*1024, rr, cr, result);
            //ce = new FMSketch(32, 16, rr, cr, result);
            //ce = new PCSA(16, 64, rr, cr, result);
            //ce = new HyperLogLog(5, 256, rr, cr, result);
            //ce = new VirtualHyperLogLog(5, 256, 8*1000000/5, rr, cr, result);
            ce = new SampledVirtualBitmap(1, 512, 2 * 1024*1024, rr, cr, result);
            ce.processData();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
