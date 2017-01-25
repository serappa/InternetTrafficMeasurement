package edu.ufl.itm.proj;

import java.util.List;

/**
 * Created by hsitas444 on 10/7/2016.
 */
public class CSVPacketTraceScanner extends PacketTraceScanner {

    public CSVPacketTraceScanner(int sourceField, int destinationField) {
        super(sourceField, destinationField);
    }

    @Override
    protected String[] scanLine(String line) {
        line = line.replace("\"","");
        return line.split(",");
    }
}
