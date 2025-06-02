package com.pbl.os.FileCompressor.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pbl.os.FileCompressor.service.CompressionService;
import com.pbl.os.FileCompressor.service.DecompressionService;

@Controller
@RequestMapping("compression")
public class FileController {

    @Autowired
    private CompressionService compressionService;

    @Autowired
    private DecompressionService decompressionService;

    @GetMapping
    public String home() {
        return "index";
    }

    // POST compress - compress file, add stats to model, return index page (no direct download)
    @PostMapping("/compress")
    public String compressFile(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        // Compress the uploaded file
        File compressed = compressionService.compressFile(file);
        if (compressed == null || !compressed.exists()) {
            model.addAttribute("errorMessage", "Compression failed or unsupported file");
            return "index";
        }

        long originalSize = file.getSize();
        long compressedSize = compressed.length();
        double compressionPercent = ((double)(originalSize - compressedSize) / originalSize) * 100.0;

        // Add stats to model
        model.addAttribute("originalSize", originalSize);
        model.addAttribute("compressedSize", compressedSize);
        model.addAttribute("compressionPercent", String.format("%.2f%%", compressionPercent));
        model.addAttribute("compressedFileName", compressed.getName());

        return "index";
    }

    // GET download endpoint to download compressed file by name
    @GetMapping("/download")
    public void downloadCompressedFile(@RequestParam("file") String filename, HttpServletResponse response) throws IOException {
        // Adjust this path depending on where compressed files are saved
        File file = new File("D:\\PBL\\OperatingSystem\\ResultFile", filename);

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
        response.setContentLengthLong(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.transferTo(response.getOutputStream());
            response.flushBuffer();
        }
    }

    @PostMapping("/decompress")
    public String decompressFile(@RequestParam("file") MultipartFile file, HttpServletResponse response, Model model) throws IOException {
        File decompressed = decompressionService.decompressFile(file);
        if (decompressed == null || !decompressed.exists()) {
            model.addAttribute("errorMessage", "Decompression failed or unsupported file");
            return "index";
        }

        // Set response headers for download
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + decompressed.getName() + "\"");
        response.setContentLengthLong(decompressed.length());

        // Write file to response output stream
        try (FileInputStream fis = new FileInputStream(decompressed)) {
            fis.transferTo(response.getOutputStream());
            response.flushBuffer();
        }
        return null; // No view to render because file download is triggered
    }
}
