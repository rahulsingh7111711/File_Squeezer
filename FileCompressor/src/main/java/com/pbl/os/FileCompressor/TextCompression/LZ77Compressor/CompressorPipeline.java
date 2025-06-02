package com.pbl.os.FileCompressor.TextCompression.LZ77Compressor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompressorPipeline {

    // Compress input file content and save compressed output
    public static void compress(String inputPath, String compressedPath) throws IOException {
        String input = FileUtil.readFile(inputPath);

        // Step 1: LZ77 compression to tokens
        List<LZ77Token> tokens = LZ77.compress(input);

        // Convert tokens to a single string with a delimiter
        StringBuilder tokenStr = new StringBuilder();
        for (LZ77Token token : tokens) {
            tokenStr.append(token.toString()).append("?");
        }

        // Step 2: Huffman compression of token string
        HuffmanCompressor huffman = new HuffmanCompressor();
        huffman.compress(tokenStr.toString(), compressedPath);
    }

    // Decompress from compressed file and return decompressed string
    public static void decompress(String compressedPath, String decompressedPath) throws IOException {
        HuffmanCompressor huffman = new HuffmanCompressor();
        
        // Decompress to get the encoded tokens string
        String tokenData = huffman.decompress(compressedPath);

        // Parse token strings back into LZ77 tokens
        List<LZ77Token> tokens = new ArrayList<>();
        for (String part : tokenData.split("\\?")) {
            if (!part.isEmpty()&&part.split(",").length==3) {
                tokens.add(LZ77Token.fromString(part));
            }
        }

        // LZ77 decompress tokens to original text
        String decompressed = LZ77.decompress(tokens);

        // Save decompressed text to file
        FileUtil.writeFile(decompressedPath, decompressed);
    }
}
