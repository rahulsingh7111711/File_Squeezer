package com.pbl.os.FileCompressor.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CompressionService {
    private final String BASE_DIR = "D:\\PBL\\OperatingSystem\\ResultFile";
    public File compressFile(MultipartFile file) {
        File compressedFile;
        try {
            File inputFile = getFile(file);
            String fileName = inputFile.getName();

            if (fileName.endsWith(".txt")) {
                compressedFile = compressText(inputFile);
            } else if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
                compressedFile = compressImage(inputFile);
            } else if (fileName.endsWith(".mp3")) {
                compressedFile = compressMp3(inputFile);
            } else if(fileName.endsWith(".mp4")){
                compressedFile = compressMp4(inputFile);
            }
             else {
                throw new UnsupportedOperationException("Unsupported file type for compression: " + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            compressedFile = null;
        }
        return compressedFile;
    }

    private File getFile(MultipartFile file){
        try {
            String name = file.getOriginalFilename();

            String baseName = name != null ? name.replaceAll("\\.[^.]+$", "") : "temp";
            String extension = name != null && name.contains(".")
                    ? name.substring(name.lastIndexOf('.'))
                    : ".tmp";

            File inputFile;
            inputFile = File.createTempFile(baseName + "_", extension);
            file.transferTo(inputFile);
            return inputFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private File compressText(File inputFile) throws IOException {
        // File compressedFile = File.createTempFile("compressed", ".txt");
        File compressedFile = new File(BASE_DIR, "compresses.txt");

        try (FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(compressedFile);
            DeflaterOutputStream dos = new DeflaterOutputStream(fos)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
        }

        return compressedFile;
    }

    private File compressImage(File inputImage) throws IOException {
        BufferedImage image = ImageIO.read(inputImage);
        if (image == null) {
            throw new IOException("Unsupported or corrupted image: " + inputImage.getName());
        }

        String format = getFileExtension(inputImage);
        long originalSize = inputImage.length();

        byte[] compressedBytes = attemptCompression(image, format);

        // Create output file with "compressed_" prefix
        String outputName = "compressed.jpg";
        File outputFile = new File(BASE_DIR, outputName);

        if (compressedBytes.length < originalSize) {
            saveToFile(compressedBytes, outputFile);
            System.out.println("Compressed: " + inputImage.getName() + " -> " + outputFile.getName());
            System.out.printf("Size reduced by %.2f%%\n", (1 - (double) outputFile.length() / originalSize) * 100);
            return outputFile;
        } else {
            System.out.println("Compression not effective. Returning original.");
            return inputImage;
        }
    }
    private String getFileExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    private byte[] attemptCompression(BufferedImage image, String format) throws IOException {
        if (format.equals("png")) {
            return compressAsPNG(image);
        } else if (format.equals("jpg") || format.equals("jpeg")) {
            return compressAsJPEG(image, 0.8f); // 80% quality
        } else {
            return compressWithDeflate(image, format);
        }
    }

    private byte[] compressAsPNG(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private byte[] compressAsJPEG(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    private byte[] compressWithDeflate(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        byte[] imageBytes = baos.toByteArray();

        ByteArrayOutputStream deflateOutput = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(deflateOutput, new Deflater(Deflater.BEST_COMPRESSION))) {
            dos.write(imageBytes);
        }
        return deflateOutput.toByteArray();
    }

    private void saveToFile(byte[] data, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(data);
        }
    }
    private File compressMp3(File inputFile) throws IOException, InterruptedException {
        // Define paths
        String inputPath = inputFile.getAbsolutePath();

        // Create compressed file in the same directory with "_compressed" suffix
        String compressedFileName = inputFile.getName().replaceAll("\\.mp3$", "") + "_compressed.mp3";
        File compressedFile = new File(BASE_DIR, "compressed.mp3");
        String compressedPath = compressedFile.getAbsolutePath();

        // Path to lame.exe — adjust if needed (you can keep this in resources or config)
        String lamePath = "src/main/resources/lame/lame.exe";

        // Build the command — here 64 kbps bitrate example, you can adjust
        ProcessBuilder pb = new ProcessBuilder(
            lamePath,
            "-b", "64",  // bitrate 64 kbps
            inputPath,
            compressedPath
        );

        // Optional: redirect error and output to your Java process console
        pb.redirectErrorStream(true);

        Process process = pb.start();

        // Read and print output (optional for debugging)
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        // Wait for the process to finish
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("LAME compression failed with exit code " + exitCode);
        }

        // Return the compressed file
        return compressedFile;
    }
    private File compressMp4(File inputFile) throws IOException {

        String compressedFileName = "compressed.mp4";
        File compressedFile = new File(BASE_DIR, compressedFileName);

        ProcessBuilder builder = new ProcessBuilder(
            "ffmpeg",  // or full path like "C:/ffmpeg/bin/ffmpeg"
            "-i", inputFile.getAbsolutePath(),
            "-vcodec", "libx264",
            "-crf", "28",
            "-preset", "fast",
            "-acodec", "aac",
            compressedFile.getAbsolutePath()
        );

        builder.redirectErrorStream(true);
        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null);
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) throw new IOException("FFmpeg failed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return compressedFile.exists() ? compressedFile : null;
    }

}
