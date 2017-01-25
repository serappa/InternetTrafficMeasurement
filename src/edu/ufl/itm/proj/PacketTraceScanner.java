package edu.ufl.itm.proj;


import java.io.*;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by hsitas444 on 10/7/2016.
 */
public abstract class PacketTraceScanner {
    private int sourceField;
    private int destinationField;
    protected abstract String[] scanLine(String line);
    public PacketTraceScanner(int sourceField, int destinationField){
        this.sourceField = sourceField;
        this.destinationField = destinationField;
    }

    public void scan(String traceFile){
        try(BufferedReader reader = new BufferedReader(new FileReader(traceFile))) {
            //Ignore the header line
            Map<InetAddress, Set<InetAddress>> data = new HashMap<>();
            int datasize=0;
            int ignoredData=0;
            String line = reader.readLine();
            System.out.println("Ignoring line1 : " + line);
            while((line = reader.readLine())!=null){
                String[] fieldVals = scanLine(line);
                datasize++;
                try {
                    InetAddress source = InetAddress.getByName(fieldVals[sourceField].trim());
                    InetAddress dest = InetAddress.getByName(fieldVals[destinationField].trim());
                    if (!data.containsKey(source))
                        data.put(source, new HashSet<>());
                    data.get(source).add(dest);
                } catch(IllegalArgumentException e){
                    ignoredData++;
                }
                if(datasize %100000 ==0)
                    System.out.println("Processed lines: "+ datasize);
            } while(line != null);
            System.out.println("Processed lines: "+ datasize);
            System.out.println("Completed reading the trace file: "+ datasize +" "+ignoredData);
            try(BufferedWriter cardw = new BufferedWriter(new FileWriter("cardinality.csv"));
                BufferedWriter reducedInput = new BufferedWriter(new FileWriter("reduced.csv")) ){
                for (Map.Entry<InetAddress,Set<InetAddress>> entries: data.entrySet()) {
                    String source = entries.getKey().toString().split("/")[1];
                    cardw.write(source);
                    cardw.write(",");
                    cardw.write(Integer.toString(entries.getValue().size()));
                    cardw.write(System.getProperty("line.separator"));
                    for(InetAddress addr: entries.getValue()){
                        reducedInput.write(source);
                        reducedInput.write(",");
                        reducedInput.write(addr.toString().split("/")[1]);
                        reducedInput.write(System.getProperty("line.separator"));
                    }
                }

            }catch(Exception e){
                System.out.println("Failed to write to cardinality.csv");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
