package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DictionaryCompression {

    public static void main(String[] args) {
        String inputFilePath = "input.txt";
        String compressedFilePath = "compressed.bin";

        try {
            String originalData = readFile(inputFilePath);
            Map<String, Integer> dictionary = createDictionary(originalData);

            long startTime = System.nanoTime();
            byte[] compressedData = compressData(originalData, dictionary);
            long endTime = System.nanoTime();

            writeBinaryFile(compressedFilePath, compressedData);
            String decompressedData = decompressData(compressedData, dictionary);

            System.out.println("Original Data: " + originalData);
            System.out.println("Decompressed Data: " + decompressedData);

            double compressionRatio = calculateCompressionRatio(originalData, compressedData);
            double compressionTime = (endTime - startTime) / 1_000_000.0;

            double originalSizeMB = originalData.getBytes().length / (double) (1024 * 1024);
            double processingSpeed = originalSizeMB / compressionTime;

            System.out.printf("Compression Ratio: %.2f\n", compressionRatio);
            System.out.println("Compression Time: " + compressionTime + " ms");
            System.out.printf("Processing Speed: %f MB/s\n", processingSpeed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void writeBinaryFile(String filePath, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }

    public static Map<String, Integer> createDictionary(String data) {
        Map<String, Integer> dictionary = new HashMap<>();
        String[] words = data.split("\\s+");

        for (String word : words) {
            if (!dictionary.containsKey(word)) {
                dictionary.put(word, dictionary.size());
            }
        }

        return dictionary;
    }

    public static byte[] compressData(String data, Map<String, Integer> dictionary) {
        StringBuilder binaryString = new StringBuilder();
        String[] words = data.split("\\s+");

        for (String word : words) {
            int index = dictionary.get(word);
            String binaryWord = String.format("%05d", Integer.parseInt(Integer.toBinaryString(index + 1)));
            binaryString.append(binaryWord);
        }

        int byteCount = (binaryString.length() + 7) / 8;
        byte[] compressedData = new byte[byteCount];
        for (int i = 0; i < binaryString.length(); i += 8) {
            int endIndex = Math.min(i + 8, binaryString.length());
            String byteString = binaryString.substring(i, endIndex);
            compressedData[i / 8] = (byte) Integer.parseInt(byteString, 2);
        }

        return compressedData;
    }

    public static String decompressData(byte[] compressedData, Map<String, Integer> dictionary) {
        StringBuilder binaryString = new StringBuilder();
        for (byte b : compressedData) {
            binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        StringBuilder decompressedData = new StringBuilder();
        Map<Integer, String> reverseDictionary = new HashMap<>();
        for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
            reverseDictionary.put(entry.getValue(), entry.getKey());
        }

        for (int i = 0; i < binaryString.length(); i += 5) {
            if (i + 5 > binaryString.length()) break;
            String code = binaryString.substring(i, i + 5);
            int index = Integer.parseInt(code, 2) - 1;
            decompressedData.append(reverseDictionary.get(index)).append(" ");
        }

        return decompressedData.toString().trim();
    }

    public static double calculateCompressionRatio(String originalData, byte[] compressedData) {
        int originalSize = originalData.getBytes().length;
        int compressedSize = compressedData.length;
        return (double) (1 - (compressedSize / (double) originalSize)) * 100;
    }

}
