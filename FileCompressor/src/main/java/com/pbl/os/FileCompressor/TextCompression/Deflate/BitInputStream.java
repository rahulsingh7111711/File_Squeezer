package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream implements AutoCloseable {
    private InputStream in;
    private int currentByte;
    private int numBitsRemaining;

    public BitInputStream(InputStream in) {
        this.in = in;
        this.currentByte = 0;
        this.numBitsRemaining = 0;
    }

    // Returns next bit (0 or 1), or -1 if EOF
    public int readBit() throws IOException {
        if (numBitsRemaining == 0) {
            currentByte = in.read();
            if (currentByte == -1) return -1;
            numBitsRemaining = 8;
        }
        numBitsRemaining--;
        return (currentByte >>> numBitsRemaining) & 1;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
