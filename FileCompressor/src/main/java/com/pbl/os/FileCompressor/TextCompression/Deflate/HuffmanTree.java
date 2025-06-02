package com.pbl.os.FileCompressor.TextCompression.Deflate;

public abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency;

    public HuffmanTree(int freq) {
        this.frequency = freq;
    }

    @Override
    public int compareTo(HuffmanTree other) {
        return this.frequency - other.frequency;
    }

}
class HuffmanLeaf extends HuffmanTree {
    public final byte value;

    public HuffmanLeaf(int freq, byte val) {
        super(freq);
        this.value = val;
    }
}

class HuffmanNode extends HuffmanTree {
    public final HuffmanTree left;
    public final HuffmanTree right;

    public HuffmanNode(HuffmanTree left, HuffmanTree right) {
        super(left.frequency + right.frequency);
        this.left = left;
        this.right = right;
    }
}
