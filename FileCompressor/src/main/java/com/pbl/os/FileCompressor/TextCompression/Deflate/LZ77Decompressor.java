package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LZ77Decompressor {
    private static final int WINDOW_SIZE = 32768;

    public static byte[] decompress(List<LZ77Token> tokens) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        LinkedList<Byte> history = new LinkedList<>();

        for (LZ77Token token : tokens) {
            if (token.isLiteral()) {
                output.write(token.getLiteral());
                addToHistory(history, token.getLiteral());
            } else {
                if (token.getOffset() > history.size()) {
                    throw new IOException("Invalid offset: " + token.getOffset());
                }

                int start = history.size() - token.getOffset();
                for (int i = 0; i < token.getLength(); i++) {
                    byte b = history.get(start + i);
                    output.write(b);
                    addToHistory(history, b);
                }
            }
        }

        return output.toByteArray();
    }

    private static void addToHistory(LinkedList<Byte> history, byte b) {
        if (history.size() == WINDOW_SIZE) {
            history.removeFirst();
        }
        history.add(b);
    }

    // Decode length from lengthCode
    public static int decodeLength(byte lengthCode) {
        return (lengthCode & 0xFF) + 3;  // Because length was stored as length - 3
    }

    // Decode offset from offsetCode
    public static int decodeOffset(byte offsetCode) {
        return (offsetCode & 0xFF) + 1;  // Because offset was stored as offset - 1
    }
}
