package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.io.IOException;

public class HuffmanTreeReader {

    /**
     * Reads a Huffman tree from the BitInputStream.
     * Tree is encoded as:
     * - bit 1: leaf node follows, then 8 bits for value
     * - bit 0: internal node, followed by left subtree, then right subtree
     */
    public static HuffmanTree readTree(BitInputStream in) throws IOException {
        int bit = in.readBit();
        if (bit == -1) {
            throw new IOException("Unexpected end of stream while reading Huffman tree");
        }
        if (bit == 1) {
            // Leaf node: read 8 bits as byte value
            int value = 0;
            for (int i = 0; i < 8; i++) {
                int b = in.readBit();
                if (b == -1) throw new IOException("Unexpected end of stream reading leaf value");
                value = (value << 1) | b;
            }
            return new HuffmanLeaf(0, (byte) value);
        } else {
            // Internal node: read left and right subtree recursively
            HuffmanTree left = readTree(in);
            HuffmanTree right = readTree(in);
            return new HuffmanNode(left, right);
        }
    }

    /**
     * Decode next byte by traversing the Huffman tree according to bits read from BitInputStream.
     */
    public static byte decodeNextByte(BitInputStream in, HuffmanTree tree) throws IOException {
        HuffmanTree current = tree;
        while (true) {
            if (current instanceof HuffmanLeaf leaf) {
                return leaf.value;
            } else if (current instanceof HuffmanNode node) {
                int bit = in.readBit();
                if (bit == -1) {
                    throw new IOException("Unexpected end of stream during Huffman decode");
                }
                current = (bit == 0) ? node.left : node.right;
            } else {
                throw new IOException("Invalid Huffman tree node type");
            }
        }
    }
}
