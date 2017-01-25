package edu.ufl.itm.proj.algo;

import edu.ufl.itm.proj.utils.ByteUtils;
import edu.ufl.itm.proj.utils.HashUtils;
import edu.ufl.itm.proj.utils.MurmurHash3;

import java.io.*;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by hsitas444 on 10/7/2016.
 */
public abstract class CardinalityEstimator {
    protected FileReader dataReader;
    protected FileReader cardinalityReader;
    protected FileWriter resultWriter;
    protected int[] randInts;

    protected int seed = 5898123;
    class Result{
        public int aV = 0;
        public double eV =0.0;

        public Result(int aV, double eV){
            this.aV = aV;
            this.eV = eV;
        }

    }


    public CardinalityEstimator(FileReader dataReader, FileReader cardinalityReader, FileWriter resultWriter) {
        this.dataReader = dataReader;
        this.cardinalityReader = cardinalityReader;
        this.resultWriter = resultWriter;
        randInts = new int[31];
        Random rand = new Random();

        for(int i=0;i<randInts.length;i++){
            randInts[i] = rand.nextInt();
        }

    }

    public void processData() throws IOException, NoSuchAlgorithmException{
        System.out.println("Starting Online processing");
        onlineOP();
        System.out.println("Online processing complete");
        preProcessOfflineOP();
        System.out.println("Offline processing started");
        offlineOP();
    }

    protected void offlineOP() throws IOException, NoSuchAlgorithmException {
        HashMap<InetAddress, Result> res = new HashMap<>();
        double mean = 0.0;
        int count = 0;
        BufferedWriter bw = new BufferedWriter(resultWriter);
        BufferedReader br = new BufferedReader(cardinalityReader);
        String line;
        while ((line = br.readLine()) != null) {
            String data[] = line.split(",");
            InetAddress source = InetAddress.getByName(data[0]);
            int actual = Integer.parseInt(data[1]);
            double estimatedCardinality = estimateCardinality(source);
            res.put(source, new Result(actual, estimatedCardinality));
            mean += estimatedCardinality;
            count++;
            if (count % 100000 == 0) {
                System.out.println("Finished Offline processing: " + count);
                bw.flush();
            }
        }
        mean = mean/count;
        double var = 0.0;
        for(Map.Entry<InetAddress, Result> e : res.entrySet()){
            var += Math.pow((e.getValue().eV - mean),2);
        }
        var = var/count;
        double varSqrt = Math.sqrt(var);
        for(Map.Entry<InetAddress, Result> e : res.entrySet()){
            bw.write(e.getKey().toString().split("/")[1]);
            bw.write(",");
            bw.write(Integer.toString(e.getValue().aV));
            bw.write(",");
            bw.write(Double.toString(e.getValue().eV));
            bw.write(",");
            bw.write(Double.toString(varSqrt /e.getValue().aV));
            bw.write("\n");
        }


    }

    protected int getMD5Hash(InetAddress addr) throws NoSuchAlgorithmException{
         return ByteUtils.bytesToInt(MessageDigest.getInstance("MD5").digest(addr.getAddress()));
    }

    protected int getFastHash(InetAddress addr){
        return HashUtils.fastHash(ByteUtils.bytesToInt(addr.getAddress()));
    }

    protected int getFastHash(int val){
        return HashUtils.fastHash(val);
    }

    protected byte[] getBigHash(InetAddress addr, int sizeInBytes) throws NoSuchAlgorithmException{
        return  getBigHash(addr.getAddress(), sizeInBytes);
    }

    protected int getMurMurHash3(InetAddress addr){
        return  getMurMurHash3(addr.getAddress());
    }

    protected int getMurMurHash3(byte[] arr){
        return MurmurHash3.murmurhash3_x86_32(arr, 0,arr.length,seed);
    }


    protected byte[] getBigHash(byte val[], int sizeInBytes) throws NoSuchAlgorithmException {
        try {
            if (sizeInBytes > 64 * randInts.length)
                throw new IllegalArgumentException("This method can only generate hash value of bytes upto: " + 16 * randInts.length);

            if(sizeInBytes <= 16){
                return MessageDigest.getInstance("SHA-512").digest(val);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(MessageDigest.getInstance("SHA-512").digest(val));
            for (int i = 0; 64 * (i + 1) < sizeInBytes; i++) {
                int addrInt = ByteUtils.bytesToInt(val);
                bos.write(MessageDigest.getInstance("SHA-512").digest(ByteUtils.intToBytes(addrInt ^ randInts[i])));
            }
            return bos.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    protected void onlineOP() throws IOException, NoSuchAlgorithmException {
        BufferedReader br = new BufferedReader(dataReader);
        String line;
        while ((line = br.readLine()) != null) {
            String addrs[] = line.split(",");
            InetAddress source = InetAddress.getByName(addrs[0]);
            InetAddress destination = InetAddress.getByName(addrs[1]);
            processOnlineData(source, destination);
        }


    }

    protected abstract void processOnlineData(InetAddress source, InetAddress destination) throws NoSuchAlgorithmException;

    protected abstract void preProcessOfflineOP();

    protected abstract double estimateCardinality(InetAddress source) throws NoSuchAlgorithmException;


}
