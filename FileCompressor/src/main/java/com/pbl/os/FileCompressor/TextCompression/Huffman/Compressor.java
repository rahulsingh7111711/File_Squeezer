package com.pbl.os.FileCompressor.TextCompression.Huffman;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Compressor {
    public static void compress(String inputPath, String outputPath) throws IOException {
        String text = Files.readString(new File(inputPath).toPath());

        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray())
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);

        HuffmanNode root = HuffmanTreeBuilder.buildTree(text, freqMap);
        Map<Character, String> huffmanCodes = new HashMap<>();
        HuffmanTreeBuilder.buildCode(root, "", huffmanCodes);

        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray())
            encoded.append(huffmanCodes.get(c));

        // Convert encoded string to BitSet
        BitSet bitSet = new BitSet(encoded.length());
        for (int i = 0; i < encoded.length(); i++) {
            if (encoded.charAt(i) == '1') {
                bitSet.set(i);
            }
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputPath))) {
            out.writeObject(huffmanCodes);      // Save Huffman codes
            out.writeInt(encoded.length());     // Save bit length
            out.writeObject(bitSet);            // Save compressed bits
        }

    }
}
