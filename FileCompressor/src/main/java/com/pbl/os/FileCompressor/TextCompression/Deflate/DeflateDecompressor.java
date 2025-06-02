package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DeflateDecompressor {

    public static void decompress(InputStream input, OutputStream output) throws IOException {
        try (BitInputStream bitIn = new BitInputStream(input)) {
            // Read Huffman trees
            HuffmanTree literalTree = readTree(bitIn);
            HuffmanTree lengthTree = readTree(bitIn);
            HuffmanTree offsetTree = readTree(bitIn);

            List<LZ77Token> tokens = new ArrayList<>();

            while (true) {
                int flag = bitIn.readBit();
                if (flag == -1) break; // EOF

                if (flag == 0) {
                    // Decode a literal by traversing literalTree
                    byte literal = decodeSymbol(bitIn, literalTree);
                    tokens.add(new LZ77Token(literal));
                } else {
                    // Decode match length and offset by traversing respective trees
                    byte length = decodeSymbol(bitIn, lengthTree);
                    byte offset = decodeSymbol(bitIn, offsetTree);
                    tokens.add(new LZ77Token(length & 0xFF, offset & 0xFF));
                }
            }

            // Decompress LZ77 tokens to raw bytes
            byte[] decompressedData = LZ77Decompressor.decompress(tokens);

            output.write(decompressedData);
        }
    }

    private static HuffmanTree readTree(BitInputStream in) throws IOException {
        int bit = in.readBit();
        if (bit == -1)
            throw new IOException("Unexpected EOF reading tree");
        if (bit == 1) {
            byte value = 0;
            for (int i = 0; i < 8; i++) {
                int b = in.readBit();
                if (b == -1)
                    throw new IOException("Unexpected EOF reading leaf value");
                value = (byte) ((value << 1) | b);
            }
            return new HuffmanLeaf(0, value);
        } else {
            HuffmanTree left = readTree(in);
            HuffmanTree right = readTree(in);
            return new HuffmanNode(left, right);
        }
    }

    private static byte decodeSymbol(BitInputStream in, HuffmanTree tree) throws IOException {
        HuffmanTree current = tree;
        while (!(current instanceof HuffmanLeaf)) {
            int bit = in.readBit();
            if (bit == -1)
                throw new IOException("Unexpected EOF while decoding symbol");
            if (bit == 0) {
                current = ((HuffmanNode) current).left;
            } else {
                current = ((HuffmanNode) current).right;
            }
        }
        return ((HuffmanLeaf) current).value;
    }
}
