package com.pbl.os.FileCompressor.ImageCompresion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.*;

public class SmartImageCompressor {

    public static void main(String[] args) {
        String dir = "D:\\PBL\\OperatingSystem\\Implementation\\ImageCompresion\\";
        String inputPath =  "D:\\PBL\\OperatingSystem\\Implementation\\ImageCompresion\\in.jpg";  // or .png, .bmp
        String outputPath =  "D:\\PBL\\OperatingSystem\\Implementation\\ImageCompresion\\compressed_in.jpg"; // compressed_input.jpg
        
        try {
            // 1. Load image and get original size
            File inputFile = new File(inputPath);
            BufferedImage image = ImageIO.read(inputFile);
            long originalSize = inputFile.length();
            System.out.println("Original size: " + originalSize + " bytes (" + getFileSizeMB(originalSize) + " MB)");

            // 2. Get file extension
            String format = inputPath.substring(inputPath.lastIndexOf('.') + 1);
            
            // 3. Compression attempt
            byte[] compressedBytes = attemptCompression(image, format);
            
            // 4. Verify and save
            if (compressedBytes.length < originalSize) {
                saveToFile(compressedBytes, outputPath);
                long compressedSize = new File(outputPath).length();
                System.out.println("Compressed size: " + compressedSize + " bytes (" + getFileSizeMB(compressedSize) + " MB)");
                System.out.printf("Reduction: %.1f%%\n", (1 - (double)compressedSize/originalSize)*100);
            } else {
                System.out.println("No compression benefit - keeping original");
            }

        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    private static byte[] attemptCompression(BufferedImage image, String format) throws IOException {
        // Strategy 1: For PNG, use optimal PNG compression
        if (format.equalsIgnoreCase("png")) {
            return compressAsPNG(image);
        }
        // Strategy 2: For JPG, use quality adjustment
        else if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
            return compressAsJPEG(image, 0.8f); // 80% quality
        }
        // Strategy 3: For others (BMP etc.), use DEFLATE
        else {
            return compressWithDeflate(image, format);
        }
    }

    private static byte[] compressAsPNG(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private static byte[] compressAsJPEG(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos); // Auto-applies JPEG compression
        return baos.toByteArray();
    }

    private static byte[] compressWithDeflate(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        byte[] imageBytes = baos.toByteArray();
        
        // Only DEFLATE if not already compressed format
        if (!format.equalsIgnoreCase("png") && !format.equalsIgnoreCase("jpg")) {
            ByteArrayOutputStream deflateOutput = new ByteArrayOutputStream();
            try (DeflaterOutputStream dos = new DeflaterOutputStream(deflateOutput, new Deflater(Deflater.BEST_COMPRESSION))) {
                dos.write(imageBytes);
            }
            return deflateOutput.toByteArray();
        }
        return imageBytes;
    }

    private static void saveToFile(byte[] data, String path) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
        }
    }

    private static String getFileSizeMB(long bytes) {
        return String.format("%.2f", (double)bytes / (1024 * 1024));
    }
}