package com.pbl.os.FileCompressor.TextCompression.LZ77Compressor;
import java.io.*;
import java.util.*;

public class HuffmanCompressor {

    private HuffmanNode root;
    private Map<Character, String> codeMap;

    private static class HuffmanNode implements Comparable<HuffmanNode> {
        char ch;
        int freq;
        HuffmanNode left, right;

        HuffmanNode(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        HuffmanNode(HuffmanNode left, HuffmanNode right) {
            this.left = left;
            this.right = right;
            this.freq = left.freq + right.freq;
        }

        @Override
        public int compareTo(HuffmanNode o) {
            return this.freq - o.freq;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    // Build Huffman tree and generate code map
    private void buildTree(String data) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : data.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (var entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            pq.add(new HuffmanNode(left, right));
        }

        root = pq.poll();
        codeMap = new HashMap<>();
        buildCodeMap(root, "");
    }

    private void buildCodeMap(HuffmanNode node, String code) {
        if (node.isLeaf()) {
            codeMap.put(node.ch, code);
            return;
        }
        buildCodeMap(node.left, code + "0");
        buildCodeMap(node.right, code + "1");
    }

    // Serialize Huffman tree to output stream (preorder)
    private void writeTree(HuffmanNode node, BitOutputStream out) throws IOException {
        if (node.isLeaf()) {
            out.writeBit(1);
            out.writeByte((byte) node.ch);
        } else {
            out.writeBit(0);
            writeTree(node.left, out);
            writeTree(node.right, out);
        }
    }

    // Deserialize Huffman tree from input stream
    private HuffmanNode readTree(BitInputStream in) throws IOException {
        int bit = in.readBit();
        if (bit == 1) {
            char ch = (char) in.readByte();
            return new HuffmanNode(ch, 0);
        } else {
            HuffmanNode left = readTree(in);
            HuffmanNode right = readTree(in);
            return new HuffmanNode(left, right);
        }
    }

    // Compress and save to file
    public void compress(String data, String outputPath) throws IOException {
        buildTree(data);

        try (BitOutputStream out = new BitOutputStream(new FileOutputStream(outputPath))) {
            writeTree(root, out);

            for (char c : data.toCharArray()) {
                String code = codeMap.get(c);
                for (char bit : code.toCharArray()) {
                    out.writeBit(bit == '1' ? 1 : 0);
                }
            }
        }
    }

    // Decompress and return original string
    public String decompress(String inputPath) throws IOException {
        try (BitInputStream in = new BitInputStream(new FileInputStream(inputPath))) {
            root = readTree(in);

            StringBuilder result = new StringBuilder();
            HuffmanNode node = root;
            int bit;
            while ((bit = in.readBit()) != -1) {
                node = (bit == 0) ? node.left : node.right;
                if (node.isLeaf()) {
                    result.append(node.ch);
                    node = root;
                }
            }
            return result.toString();
        }
    }

    // Helper classes for bitwise IO follow below
    private static class BitOutputStream implements Closeable {
        private OutputStream out;
        private int currentByte;
        private int numBitsFilled;

        BitOutputStream(OutputStream out) {
            this.out = out;
            this.currentByte = 0;
            this.numBitsFilled = 0;
        }

        void writeBit(int bit) throws IOException {
            currentByte = (currentByte << 1) | (bit & 1);
            numBitsFilled++;
            if (numBitsFilled == 8) {
                out.write(currentByte);
                numBitsFilled = 0;
                currentByte = 0;
            }
        }

        void writeByte(byte b) throws IOException {
            if (numBitsFilled == 0) {
                out.write(b);
            } else {
                for (int i = 7; i >= 0; i--) {
                    writeBit((b >> i) & 1);
                }
            }
        }

        @Override
        public void close() throws IOException {
            while (numBitsFilled != 0) {
                writeBit(0);
            }
            out.close();
        }
    }

    private static class BitInputStream implements Closeable {
        private InputStream in;
        private int currentByte;
        private int numBitsRemaining;

        BitInputStream(InputStream in) {
            this.in = in;
            this.currentByte = 0;
            this.numBitsRemaining = 0;
        }

        int readBit() throws IOException {
            if (numBitsRemaining == 0) {
                currentByte = in.read();
                if (currentByte == -1) {
                    return -1;
                }
                numBitsRemaining = 8;
            }
            numBitsRemaining--;
            return (currentByte >> numBitsRemaining) & 1;
        }

        int readByte() throws IOException {
            int result = 0;
            for (int i = 0; i < 8; i++) {
                int bit = readBit();
                if (bit == -1) throw new EOFException();
                result = (result << 1) | bit;
            }
            return result;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}
