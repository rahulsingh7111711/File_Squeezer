package com.pbl.os.FileCompressor.TextCompression.Huffman;

import java.io.Serializable;

public class HuffmanNode implements Comparable<HuffmanNode>, Serializable {
    char ch;
    int freq;
    HuffmanNode left, right;

    public HuffmanNode(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return Integer.compare(this.freq, o.freq);
    }
}
