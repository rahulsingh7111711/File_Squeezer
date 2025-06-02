package com.pbl.os.FileCompressor.TextCompression.Huffman;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Decompressor {
    public static void decompress(String inputPath, String outputPath) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(inputPath))) {
            Map<Character, String> huffmanCodes = (Map<Character, String>) in.readObject();
            int bitLength = in.readInt();
            BitSet bitSet = (BitSet) in.readObject();

            Map<String, Character> reverseCodes = new HashMap<>();
            for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
                reverseCodes.put(entry.getValue(), entry.getKey());
            }

            StringBuilder current = new StringBuilder();
            StringBuilder decoded = new StringBuilder();

            for (int i = 0; i < bitLength; i++) {
                current.append(bitSet.get(i) ? '1' : '0');
                if (reverseCodes.containsKey(current.toString())) {
                    decoded.append(reverseCodes.get(current.toString()));
                    current.setLength(0);
                }
            }

            Files.write(new File(outputPath).toPath(), decoded.toString().getBytes());
            System.out.println("✅ File decompressed successfully!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
