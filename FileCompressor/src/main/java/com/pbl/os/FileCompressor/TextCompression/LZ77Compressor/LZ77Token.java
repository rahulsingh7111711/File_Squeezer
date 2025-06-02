package com.pbl.os.FileCompressor.TextCompression.LZ77Compressor;
public class LZ77Token {
    public final int offset;
    public final int length;
    public final char nextChar;

    public LZ77Token(int offset, int length, char nextChar) {
        this.offset = offset;
        this.length = length;
        this.nextChar = nextChar;
    }

    // Format: offset,length,nextChar
    @Override
    public String toString() {
        return offset + "," + length + "," + (int) nextChar;
    }

    // Parse token from string format "offset,length,nextCharCode"
    public static LZ77Token fromString(String s) {

        String[] parts = s.split(",");
        
        int offset = Integer.parseInt(parts[0]);
        int length = Integer.parseInt(parts[1]);
        char nextChar = (char) Integer.parseInt(parts[2]);
        return new LZ77Token(offset, length, nextChar);
    }
}
