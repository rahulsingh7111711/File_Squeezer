package com.pbl.os.FileCompressor.TextCompression;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import com.pbl.os.FileCompressor.TextCompression.LZ77Compressor.CompressorPipeline;

public class TextCompression {
    private static final String BASE_DIR = "D:\\PBL\\OperatingSystem\\Implementation\\TextCompression\\";
    private static final String INPUT_FILE = BASE_DIR + "input1.txt";
    private static final String[] COMPRESSED_FILES = {
        BASE_DIR + "LZ77Compressor\\compressed.lz77",
        BASE_DIR + "Huffman\\compressed.huff",
        BASE_DIR + "Deflate\\compressed.deflate"
    };
    private static final String BEST_COMPRESSED_FILE = BASE_DIR + "compressed.txt";
    private static final String DECOMPRESSED_FILE = BASE_DIR + "decompressed.txt";
    private static String bestAlgorithm = "";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n===== TEXT COMPRESSION BENCHMARK =====");
            System.out.println("1. Compress with best algorithm");
            System.out.println("2. Decompress last compressed file");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1:
                    runBenchmark();
                    break;
                case 2:
                    decompressBest();
                    break;
                case 3:
                    System.out.println("Exiting program...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    private static void runBenchmark() {
        try {
            // Read input file
            byte[] inputData = Files.readAllBytes(Paths.get(INPUT_FILE));
            long originalSize = inputData.length;
            
            System.out.println("\n⚡ Running compression benchmark...");
            System.out.printf("📄 Original file size: %,d bytes\n\n", originalSize);
            
            CompressionResult[]results = new CompressionResult[3];
            Thread[]threads = new Thread[3];
            threads[0] = new Thread(()->{
                    try {
                        results[0]=testLZ77(inputData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            });
            threads[1] = new Thread(()->{
                    try {
                        results[1]=testHuffman(inputData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            });
            threads[2] = new Thread(()->{
                    try {
                        results[2]=testDeflate(inputData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            });

            for(Thread t : threads){
                t.start();
            }
            for(Thread t : threads){
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Find best algorithm
            CompressionResult bestResult = Arrays.stream(results)
                .min(Comparator.comparingLong(r -> r.compressedSize))
                .get();
            
            bestAlgorithm = bestResult.algorithmName;
            
            // Print results table
            printResultsTable(results, originalSize);
            
            // Save best compressed file
            Files.copy(Paths.get(bestResult.outputFile), Paths.get(BEST_COMPRESSED_FILE), 
                StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("File compressed successfully!");
            
            System.out.println("\n Best algorithm: " + bestAlgorithm + 
                String.format(" (%.2f%% reduction)", 
                100 - ((double)bestResult.compressedSize / originalSize * 100)));
            System.out.println(" Saved best compressed file as: " + BEST_COMPRESSED_FILE);
            
        } catch (IOException e) {
            System.err.println("Error during benchmark: " + e.getMessage());
        }
    }
    
    private static void printResultsTable(CompressionResult[] results, long originalSize) {
        System.out.println("+------------+------------+----------------+----------------+");
        System.out.println("| Algorithm  | Time (ms)  | Compressed Size | Reduction (%)  |");
        System.out.println("+------------+------------+----------------+----------------+");
        
        for (CompressionResult result : results) {
            double reduction = 100 - ((double)result.compressedSize / originalSize * 100);
            System.out.printf("| %-10s | %-10d | %,14d | %13.2f%% |\n",
                result.algorithmName,
                result.timeTaken,
                result.compressedSize,
                reduction);
        }
        
        System.out.println("+------------+------------+----------------+----------------+");
    }
    
    private static CompressionResult testLZ77(byte[] inputData) throws IOException {
        long startTime = System.currentTimeMillis();
        String outputFile = COMPRESSED_FILES[0];
        
        CompressorPipeline.compress(
            INPUT_FILE, outputFile);
        
        long compressedSize = new File(outputFile).length();
        return new CompressionResult(
            "LZ77",
            System.currentTimeMillis() - startTime,
            compressedSize,
            outputFile
        );
    }
    
    private static CompressionResult testHuffman(byte[] inputData) throws IOException {
        long startTime = System.currentTimeMillis();
        String outputFile = COMPRESSED_FILES[1];
        
        com.pbl.os.FileCompressor.TextCompression.Huffman.Compressor.compress(
            INPUT_FILE, outputFile);
        
        long compressedSize = new File(outputFile).length();
        return new CompressionResult(
            "Huffman",
            System.currentTimeMillis() - startTime,
            compressedSize,
            outputFile
        );
    }
    
    private static CompressionResult testDeflate(byte[] inputData) throws IOException {
        long startTime = System.currentTimeMillis();
        String outputFile = COMPRESSED_FILES[2];
        
        try (FileInputStream fis = new FileInputStream(INPUT_FILE);
             FileOutputStream fos = new FileOutputStream(outputFile);
             DeflaterOutputStream dos = new DeflaterOutputStream(fos)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
        }
        
        long compressedSize = new File(outputFile).length();
        return new CompressionResult(
            "Deflate",
            System.currentTimeMillis() - startTime,
            compressedSize,
            outputFile
        );
    }
    
    private static void decompressBest() {
        if (bestAlgorithm.isEmpty()) {
            System.out.println("⚠️ No compression has been run yet!");
            return;
        }
        
        try {
            System.out.println("\n🔍 Decompressing using " + bestAlgorithm + " algorithm...");
            long startTime = System.currentTimeMillis();
            
            switch (bestAlgorithm) {
                case "LZ77":
                    CompressorPipeline.decompress(
                        BEST_COMPRESSED_FILE, DECOMPRESSED_FILE);
                    break;
                    
                case "Huffman":
                    com.pbl.os.FileCompressor.TextCompression.Huffman.Decompressor.decompress(
                        BEST_COMPRESSED_FILE, DECOMPRESSED_FILE);
                    break;
                    
                case "Deflate":
                    try (InputStream is = new InflaterInputStream(
                            new FileInputStream(BEST_COMPRESSED_FILE));
                         OutputStream os = new FileOutputStream(DECOMPRESSED_FILE)) {
                        is.transferTo(os);
                    }
                    break;
            }
            
            long timeTaken = System.currentTimeMillis() - startTime;
            System.out.println("✅ Decompression completed in " + timeTaken + " ms");
            System.out.println("📄 Decompressed file saved as: " + DECOMPRESSED_FILE);
            
            // Verify decompression
            verifyDecompression();
            
        } catch (IOException e) {
            System.err.println("❌ Error during decompression: " + e.getMessage());
        }
    }
    
    private static void verifyDecompression() throws IOException {
        long originalSize = new File(INPUT_FILE).length();
        long decompressedSize = new File(DECOMPRESSED_FILE).length();
        
        if (originalSize == decompressedSize) {
            System.out.println("✔️ Verification: Decompressed file matches original size");
        } else {
            System.out.println("⚠️ Warning: Decompressed file size differs from original!");
            System.out.println("   Original: " + originalSize + " bytes");
            System.out.println("   Decompressed: " + decompressedSize + " bytes");
        }
    }
    
    private static class CompressionResult {
        String algorithmName;
        long timeTaken;
        long compressedSize;
        String outputFile;
        
        CompressionResult(String algorithmName, long timeTaken, 
                         long compressedSize, String outputFile) {
            this.algorithmName = algorithmName;
            this.timeTaken = timeTaken;
            this.compressedSize = compressedSize;
            this.outputFile = outputFile;
        }
    }
}
