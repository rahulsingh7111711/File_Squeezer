package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.io.*;
import java.util.Scanner;

public class Main {
    // Predefined file paths
    private static final String DIR = "D:\\PBL\\OperatingSystem\\Implementation\\TextCompression\\Deflate\\";
    private static final String INPUT_FILE = DIR + "input.txt";
    private static final String COMPRESSED_FILE = DIR + "compressed.deflate";
    private static final String DECOMPRESSED_FILE = DIR + "decompressed.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    compressWithCustomDeflate();
                    break;
                case 2:
                    decompressWithCustomDeflate();
                    break;
                case 3:
                    showCompressionStats();
                    break;
                case 4:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while (choice != 4);

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\nCUSTOM DEFLATE COMPRESSION MENU");
        System.out.println("1. Compress file (Custom Deflate)");
        System.out.println("2. Decompress file (Custom Deflate)");
        System.out.println("3. Show compression statistics");
        System.out.println("4. Exit");
    }

    private static void compressWithCustomDeflate() {
        try {
            System.out.println("\nCompressing " + INPUT_FILE + " to " + COMPRESSED_FILE);
            long startTime = System.currentTimeMillis();

            try (FileInputStream fis = new FileInputStream(INPUT_FILE);
                 FileOutputStream fos = new FileOutputStream(COMPRESSED_FILE)) {
                DeflateCompressor.compress(fis, fos);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Compression completed in " + (endTime - startTime) + " ms");

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error during compression: " + e.getMessage());
        }
    }

    private static void decompressWithCustomDeflate() {
        try {
            System.out.println("\nDecompressing " + COMPRESSED_FILE + " to " + DECOMPRESSED_FILE);
            long startTime = System.currentTimeMillis();

            try (FileInputStream fis = new FileInputStream(COMPRESSED_FILE);
                 FileOutputStream fos = new FileOutputStream(DECOMPRESSED_FILE)) {
                DeflateDecompressor.decompress(fis, fos);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Decompression completed in " + (endTime - startTime) + " ms");

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error during decompression: " + e.getMessage());
        }
    }

    private static void showCompressionStats() {
        try {
            File original = new File(INPUT_FILE);
            File compressed = new File(COMPRESSED_FILE);
            File decompressed = new File(DECOMPRESSED_FILE);

            if (!original.exists() || !compressed.exists() || !decompressed.exists()) {
                System.out.println("\nPlease compress and decompress files first to view statistics");
                return;
            }

            long originalSize = original.length();
            long compressedSize = compressed.length();
            long decompressedSize = decompressed.length();

            System.out.println("\nCompression Statistics:");
            System.out.printf("Original size: %,d bytes\n", originalSize);
            System.out.printf("Compressed size: %,d bytes\n", compressedSize);
            System.out.printf("Decompressed size: %,d bytes\n", decompressedSize);

            double compressionRatio = 100 - ((double) compressedSize / originalSize * 100);
            System.out.printf("Compression ratio: %.2f%%\n", compressionRatio);

            if (originalSize == decompressedSize) {
                System.out.println("Status: Decompression successful (file sizes match)");
            } else {
                System.out.println("Warning: Decompressed file size differs from original by " +
                        (decompressedSize - originalSize) + " bytes");
            }

        } catch (Exception e) {
            System.err.println("Error comparing files: " + e.getMessage());
        }
    }
}
