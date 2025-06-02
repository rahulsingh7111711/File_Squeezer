package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream implements AutoCloseable {
    private OutputStream out;
    private int currentByte;
    private int numBitsFilled;

    public BitOutputStream(OutputStream out) {
        this.out = out;
        this.currentByte = 0;
        this.numBitsFilled = 0;
    }

    public void writeBit(int bit) throws IOException {
        if (bit != 0 && bit != 1)
            throw new IllegalArgumentException("Bit must be 0 or 1");
        currentByte = (currentByte << 1) | bit;
        numBitsFilled++;
        if (numBitsFilled == 8) {
            out.write(currentByte);
            numBitsFilled = 0;
            currentByte = 0;
        }
    }

    // Write multiple bits given as a string "1010"
    public void writeBits(String bits) throws IOException {
        for (char c : bits.toCharArray()) {
            if (c == '0') writeBit(0);
            else if (c == '1') writeBit(1);
            else throw new IllegalArgumentException("Bits string must contain only 0 or 1");
        }
    }

    // Flush remaining bits padded with zeros
    public void flush() throws IOException {
        if (numBitsFilled > 0) {
            currentByte <<= (8 - numBitsFilled);
            out.write(currentByte);
            numBitsFilled = 0;
            currentByte = 0;
        }
        out.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }
}
