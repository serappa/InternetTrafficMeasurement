package edu.ufl.itm.proj;

/**
 * Created by hsitas444 on 10/7/2016.
 */
public class TAFormatScanner extends PacketTraceScanner {

    public TAFormatScanner(int sourceField, int destinationField) {
        super(sourceField, destinationField);
    }

    @Override
    protected String[] scanLine(String line) {
        return line.split("    ");
    }
}