package com.pbl.os.FileCompressor.TextCompression.Deflate;

import java.util.ArrayList;
import java.util.List;

public class LZ77Compressor {

    private static final int WINDOW_SIZE = 4096;
    private static final int MAX_MATCH_LENGTH = 18;
    private static final int MIN_MATCH_LENGTH = 3;

    public static List<LZ77Token> compress(byte[] data) {
        List<LZ77Token> tokens = new ArrayList<>();
        int pos = 0;

        while (pos < data.length) {
            int matchLength = 0;
            int matchOffset = 0;

            int startWindow = Math.max(0, pos - WINDOW_SIZE);

            for (int j = startWindow; j < pos; j++) {
                int length = 0;
                while (length < MAX_MATCH_LENGTH && pos + length < data.length
                        && data[j + length] == data[pos + length]) {
                    length++;
                }
                if (length >= MIN_MATCH_LENGTH && length > matchLength) {
                    matchLength = length;
                    matchOffset = pos - j;
                }
            }

            if (matchLength >= MIN_MATCH_LENGTH) {
                tokens.add(new LZ77Token(matchLength, matchOffset));
                pos += matchLength;
            } else {
                tokens.add(new LZ77Token(data[pos]));
                pos++;
            }
        }

        return tokens;
    }
}
