package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeflateCompressor {

    public static void compress(InputStream input, OutputStream output) throws IOException {
        byte[] data = input.readAllBytes();

        // 1. LZ77 compress
        List<LZ77Token> tokens = LZ77Compressor.compress(data);

        // 2. Frequency tables for literals and matches
        Map<Byte, Integer> literalFreq = new HashMap<>();
        Map<Integer, Integer> lengthFreq = new HashMap<>();
        Map<Integer, Integer> offsetFreq = new HashMap<>();

        for (LZ77Token token : tokens) {
            if (token.isLiteral()) {
                literalFreq.merge(token.getLiteral(), 1, Integer::sum);
            } else {
                lengthFreq.merge(token.getLength(), 1, Integer::sum);
                offsetFreq.merge(token.getOffset(), 1, Integer::sum);
            }
        }

        // 3. Build Huffman trees
        HuffmanTree literalTree = HuffmanCoder.buildTree(literalFreq);
        HuffmanTree lengthTree = HuffmanCoder.buildTree(intToByteMap(lengthFreq));
        HuffmanTree offsetTree = HuffmanCoder.buildTree(intToByteMap(offsetFreq));

        Map<Byte, String> literalCodes = HuffmanCoder.buildCodeMap(literalTree);
        Map<Byte, String> lengthCodes = HuffmanCoder.buildCodeMap(lengthTree);
        Map<Byte, String> offsetCodes = HuffmanCoder.buildCodeMap(offsetTree);

        try (BitOutputStream bitOut = new BitOutputStream(output)) {
            // Write Huffman trees
            writeTree(bitOut, literalTree);
            writeTree(bitOut, lengthTree);
            writeTree(bitOut, offsetTree);

            // Write tokens: first bit 0 = literal, 1 = match
            for (LZ77Token token : tokens) {
                if (token.isLiteral()) {
                    bitOut.writeBit(0);
                    String code = literalCodes.get(token.getLiteral());
                    bitOut.writeBits(code);
                } else {
                    bitOut.writeBit(1);
                    String lengthCode = lengthCodes.get((byte) token.getLength());
                    String offsetCode = offsetCodes.get((byte) token.getOffset());
                    bitOut.writeBits(lengthCode);
                    bitOut.writeBits(offsetCode);
                }
            }
        }
    }

    private static Map<Byte, Integer> intToByteMap(Map<Integer, Integer> map) {
        Map<Byte, Integer> result = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            result.put(entry.getKey().byteValue(), entry.getValue());
        }
        return result;
    }

    private static void writeTree(BitOutputStream out, HuffmanTree tree) throws IOException {
        if (tree instanceof HuffmanLeaf leaf) {
            out.writeBit(1);
            byte value = leaf.value;
            for (int i = 7; i >= 0; i--) {
                out.writeBit((value >> i) & 1);
            }
        } else if (tree instanceof HuffmanNode node) {
            out.writeBit(0);
            writeTree(out, node.left);
            writeTree(out, node.right);
        }
    }
}
