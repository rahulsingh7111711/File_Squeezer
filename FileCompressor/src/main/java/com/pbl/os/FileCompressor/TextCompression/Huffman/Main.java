package com.pbl.os.FileCompressor.TextCompression.Huffman;
import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String dir = "D:\\PBL\\OperatingSystem\\Implementation\\TextCompression\\Huffman\\";
        String inputPath = dir + "input.txt";
        String compressedPath = dir + "compressed.huff";
        String decompressedPath = dir + "decompressed.txt";

        while (true) {
            System.out.println("\n====== Huffman File Compressor ======");
            System.out.println("1. Compress File");
            System.out.println("2. Decompress File");
            System.out.println("3. Compare File Sizes");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        Compressor.compress(inputPath, compressedPath);
                        break;
                    case 2:
                        Decompressor.decompress(compressedPath, decompressedPath);
                        break;
                    case 3:
                        File original = new File(inputPath);
                        File compressed = new File(compressedPath);
                        System.out.println("📄 Original size:   " + original.length() + " bytes");
                        System.out.println("📦 Compressed size: " + compressed.length() + " bytes");

                        double reduction = 100.0 * (1 - (double) compressed.length() / original.length());
                        System.out.printf("📉 Size reduced by: %.2f%%\n", reduction);
                        break;
                    case 0:
                        System.out.println("👋 Exiting. Goodbye!");
                        return;
                    default:
                        System.out.println("⚠️ Invalid option. Try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
