package com.pbl.os.FileCompressor.TextCompression.Huffman;

import java.util.*;

public class HuffmanTreeBuilder {
    public static HuffmanNode buildTree(String text, Map<Character, Integer> freqMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }

        return pq.poll();
    }

    public static void buildCode(HuffmanNode node, String code, Map<Character, String> huffmanCodes) {
        if (node == null) return;
        if (node.isLeaf()) {
            huffmanCodes.put(node.ch, code);
            return;
        }
        buildCode(node.left, code + "0", huffmanCodes);
        buildCode(node.right, code + "1", huffmanCodes);
    }
}
