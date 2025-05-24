# 📦 File Compression Suite (Java)

A powerful **File Compression Project** developed in **Java** that supports compression and decompression for various file types including **text**, **images**, **documents**, **PDFs**, **audio**, and **video**. The system leverages multiple algorithms such as **Huffman Coding**, **LZ77**, and **Run-Length Encoding (RLE)** to effectively minimize file sizes while maintaining performance and data integrity.

---

## 🚀 Features

- 📝 **Text Compression** using **Huffman Coding** and **LZ77**.
- 🖼️ **Image Compression** using **Run-Length Encoding (RLE)** and pixel value pattern recognition.
- 📄 **Document and PDF Compression** using structural analysis and whitespace optimization.
- 🔊🎞️ **Audio and Video Compression** with wrapper methods to interface with codec libraries and metadata reduction.
- ⚙️ Easy-to-use **command-line interface** with real-time compression stats.
- 🧩 Modular codebase to add support for new file types and algorithms.

---

## 🛠️ Technologies Used

- **Java SE**
- **Object-Oriented Programming (OOP)**
- **Java I/O and File Handling**
- **Algorithm Design (Huffman, LZ77, RLE)**

---

## 📁 Supported File Types

- `.txt`
- `.jpg`, `.png`
- `.docx`, `.pdf`
- `.mp3`, `.wav`
- `.mp4`, `.mkv`

---

## 🧠 How It Works

Each file type goes through a specialized compression pipeline:

- **Text Files**: Frequency table → Huffman Tree → Bitstream encoding.
- **Images**: Pixel analysis → Run-Length Encoding.
- **Documents/PDFs**: Redundant pattern detection → Compression logic.
- **Audio/Video**: Interface with standard codecs → Apply custom metadata and frame-size optimizations.

---
