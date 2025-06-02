package com.pbl.os.FileCompressor.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DecompressionService {
    private final String BASE_DIR = "D:\\PBL\\OperatingSystem\\ResultFile";
    public File decompressFile(MultipartFile file) {
        File decompressedFile;
        try {
            File inputFile = getFile(file);
            String fileName = inputFile.getName();

            if (fileName.endsWith(".txt")) {
                decompressedFile = decompressText(inputFile);
            } else if (fileName.endsWith(".jpg")) {
                decompressedFile = decompressImage(inputFile);
            } else if (fileName.endsWith(".mp3")) {
                decompressedFile = decompressMp3(inputFile);
            } else if(fileName.endsWith(".mp4")){
                decompressedFile = decompressMp4(inputFile);
            }
            else {
                throw new UnsupportedOperationException("Unsupported file type for decompression: " + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            decompressedFile = null;
        }
        return decompressedFile;
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

    public File decompressText(File compressedFile) throws IOException {

        String decompressedName = "decompressed.txt";
        File decompressedFile = new File(BASE_DIR, decompressedName);

        try (FileInputStream fis = new FileInputStream(compressedFile);
             InflaterInputStream iis = new InflaterInputStream(fis);
             FileOutputStream fos = new FileOutputStream(decompressedFile)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }

        return decompressedFile;
    }
    public File decompressImage(File compressedFile) throws IOException {
    // Read the image
        BufferedImage image = ImageIO.read(compressedFile);
        if (image == null) {
            throw new IOException("File is not a valid image: " + compressedFile.getName());
        }

        // Create new filename with "decompressed_" prefix
        String originalName = compressedFile.getName();
        String decompressedName = "decompressed.jpg";

        File outputFile = new File(BASE_DIR, decompressedName);

        // Get file extension
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex + 1);
        }

        // Write the image back to the new file
        ImageIO.write(image, extension, outputFile);

        return outputFile;
    }
    public File decompressMp3(File mp3File) throws IOException {
        
        String decompressedName = "decompressed.mp3";

        File decompressedFile = new File(BASE_DIR, decompressedName);

        // Copy file content
        try (FileInputStream fis = new FileInputStream(mp3File);
            FileOutputStream fos = new FileOutputStream(decompressedFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        return decompressedFile;
    }
    private File decompressMp4(File inputFile) throws IOException {

        String outputFileName = "decompressed.mp4";
        File outputFile = new File(BASE_DIR, outputFileName);

        ProcessBuilder builder = new ProcessBuilder(
            "ffmpeg",
            "-i", inputFile.getAbsolutePath(),
            "-vcodec", "libx264",
            "-crf", "18", // Higher quality
            "-preset", "slow",
            "-acodec", "aac",
            outputFile.getAbsolutePath()
        );

        builder.redirectErrorStream(true);
        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null);
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg decompression failed");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return outputFile.exists() ? outputFile : null;
    }

}
