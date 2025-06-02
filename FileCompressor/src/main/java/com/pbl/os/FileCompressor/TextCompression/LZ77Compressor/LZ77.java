package com.pbl.os.FileCompressor.TextCompression.LZ77Compressor;
import java.util.ArrayList;
import java.util.List;

public class LZ77 {
    private static final int WINDOW_SIZE = 4096;  // sliding window size
    private static final int LOOKAHEAD_BUFFER_SIZE = 18;

    // Compress input string into list of LZ77Tokens
    public static List<LZ77Token> compress(String input) {
        List<LZ77Token> tokens = new ArrayList<>();
        int pos = 0;

        while (pos < input.length()) {
            int matchLength = 0;
            int matchOffset = 0;
            int startSearch = Math.max(0, pos - WINDOW_SIZE);

            for (int j = startSearch; j < pos; j++) {
                int length = 0;
                while (length < LOOKAHEAD_BUFFER_SIZE &&
                        pos + length < input.length() &&
                        input.charAt(j + length) == input.charAt(pos + length)) {
                    length++;
                }

                if (length > matchLength) {
                    matchLength = length;
                    matchOffset = pos - j;
                }
            }

            char nextChar = (pos + matchLength < input.length()) ? input.charAt(pos + matchLength) : '\0';

            tokens.add(new LZ77Token(matchOffset, matchLength, nextChar));
            pos += matchLength + 1;
        }
        return tokens;
    }

    // Decompress list of LZ77Tokens to original string
    public static String decompress(List<LZ77Token> tokens) {
        StringBuilder output = new StringBuilder();

        for (LZ77Token token : tokens) {
            int start = output.length() - token.offset;
            for (int i = 0; i < token.length; i++) {
                output.append(output.charAt(start + i));
            }
            if (token.nextChar != '\0') {
                output.append(token.nextChar);
            }
        }

        return output.toString();
    }
}
