package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanCoder {

    public static HuffmanTree buildTree(Map<Byte, Integer> freq) {
        PriorityQueue<HuffmanTree> pq = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : freq.entrySet()) {
            pq.offer(new HuffmanLeaf(entry.getValue(), entry.getKey()));
        }

        while (pq.size() > 1) {
            HuffmanTree left = pq.poll();
            HuffmanTree right = pq.poll();
            pq.offer(new HuffmanNode(left, right));
        }

        return pq.poll();
    }

    public static void buildCodeMap(HuffmanTree tree, String prefix, Map<Byte, String> map) {
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf) tree;
            map.put(leaf.value, prefix);
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode) tree;
            buildCodeMap(node.left, prefix + '0', map);
            buildCodeMap(node.right, prefix + '1', map);
        }
    }

    public static Map<Byte, String> buildCodeMap(HuffmanTree tree) {
        Map<Byte, String> map = new HashMap<>();
        buildCodeMap(tree, "", map);
        return map;
    }
}
