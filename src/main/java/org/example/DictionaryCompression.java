package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DictionaryCompression {

    public static void main(String[] args) {
        String inputFilePath = "input.txt";
        String compressedFilePath = "compressed.txt";

        try {
            String originalData = readFile(inputFilePath);
            Map<String, String> dictionary = createDictionary(originalData);

            long startTime = System.nanoTime();
            String compressedData = compressData(originalData, dictionary);
            long endTime = System.nanoTime();

            writeFile(compressedFilePath, compressedData);
            String decompressedData = decompressData(compressedData, dictionary);

            System.out.println("Original Data: " + originalData);
            System.out.println("Compressed Data: " + compressedData);
            System.out.println("Decompressed Data: " + decompressedData);

            double compressionRatio = calculateCompressionRatio(originalData, compressedData);
            double compressionTime = (endTime - startTime) / 1_000_000.0;

            double originalSizeMB = originalData.getBytes().length / (double) (1024 * 1024);
            double processingSpeed = originalSizeMB / compressionTime;

            System.out.printf("Compression Ratio: %.2f\n" , compressionRatio);
            System.out.println("Compression Time: " + compressionTime + " ms");
            System.out.printf("Processing Speed: %f MB/s\n", processingSpeed);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void writeFile(String filePath, String data) throws IOException {
        Files.write(Paths.get(filePath), data.getBytes());
    }

    public static Map<String, String> createDictionary(String data) {
        Map<String, String> dictionary = new HashMap<>();
        String[] words = data.split("\\s+");

        for (String word : words) {
            if (!dictionary.containsKey(word)) {
                dictionary.put(word, "#" + dictionary.size());
            }
        }

        return dictionary;
    }

    public static String compressData(String data, Map<String, String> dictionary) {
        String[] words = data.split("\\s+");
        StringBuilder compressedData = new StringBuilder();

        for (String word : words) {
            compressedData.append(dictionary.get(word)).append(" ");
        }

        return compressedData.toString().trim();
    }

    public static String decompressData(String compressedData, Map<String, String> dictionary) {
        String[] codes = compressedData.split("\\s+");
        StringBuilder decompressedData = new StringBuilder();

        for (String code : codes) {
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                if (entry.getValue().equals(code)) {
                    decompressedData.append(entry.getKey()).append(" ");
                    break;
                }
            }
        }

        return decompressedData.toString().trim();
    }

    public static double calculateCompressionRatio(String originalData, String compressedData) {
        int originalSize = originalData.getBytes().length;
        int compressedSize = compressedData.getBytes().length;
        return (double) (1 - (compressedSize / (double) originalSize)) * 100;
    }

}
