package com.pbl.os.FileCompressor.TextCompression.Deflate;

public class LZ77Token {
    private final boolean isLiteral;
    private final byte literal;
    private final int length;
    private final int offset;

    // Literal constructor
    public LZ77Token(byte literal) {
        this.isLiteral = true;
        this.literal = literal;
        this.length = 0;
        this.offset = 0;
    }

    // Match constructor
    public LZ77Token(int length, int offset) {
        this.isLiteral = false;
        this.literal = 0;
        this.length = length;
        this.offset = offset;
    }

    public boolean isLiteral() {
        return isLiteral;
    }

    public byte getLiteral() {
        return literal;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }
}
