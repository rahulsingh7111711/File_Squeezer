package com.pbl.os.FileCompressor.TextCompression.LZ77Compressor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    // Read entire file content as a String
    public static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    // Write entire content String to a file
    public static void writeFile(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes());
    }
}
