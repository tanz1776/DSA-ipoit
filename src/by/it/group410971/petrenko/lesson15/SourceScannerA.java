package by.it.group410971.petrenko.lesson15;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class SourceScannerA {
    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        List<FileData> files = new ArrayList<>();

        try {
            Files.walk(Paths.get(src))
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> processFile(p, src, files));

            files.sort((a, b) -> {
                if (a.size != b.size) return Integer.compare(a.size, b.size);
                return a.relPath.compareTo(b.relPath);
            });

            files.forEach(f -> System.out.println(f.size + " " + f.relPath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class FileData {
        int size;
        String relPath;
        FileData(int size, String relPath) {
            this.size = size;
            this.relPath = relPath;
        }
    }

    static void processFile(Path file, String src, List<FileData> files) {
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);

            if (content.contains("@Test") || content.contains("org.junit.Test")) {
                return;
            }

            content = removePackageAndImports(content);
            content = trimWhitespace(content);

            files.add(new FileData(content.getBytes(StandardCharsets.UTF_8).length,
                    src.substring(src.indexOf("src")).length() > 0 ?
                            file.toString().substring(file.toString().indexOf("src") + 4) :
                            file.toString()));

        } catch (MalformedInputException e) {
            try {
                String content = Files.readString(file, StandardCharsets.ISO_8859_1);
                if (content.contains("@Test") || content.contains("org.junit.Test")) return;
                content = removePackageAndImports(content);
                content = trimWhitespace(content);
                files.add(new FileData(content.getBytes(StandardCharsets.ISO_8859_1).length,
                        src.substring(src.indexOf("src")).length() > 0 ?
                                file.toString().substring(file.toString().indexOf("src") + 4) :
                                file.toString()));
            } catch (IOException ex) {
                return;
            }
        } catch (IOException e) {
            return;
        }
    }

    static String removePackageAndImports(String content) {
        StringBuilder sb = new StringBuilder();
        boolean inImport = false;
        int braceLevel = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (i <= content.length() - 6 && content.startsWith("import", i)) {
                inImport = true;
                continue;
            }

            if (i <= content.length() - 7 && content.startsWith("package", i)) {
                inImport = true;
                continue;
            }

            if (inImport) {
                if (c == ';') {
                    inImport = false;
                }
                continue;
            }

            sb.append(c);
        }

        return sb.toString();
    }

    static String trimWhitespace(String content) {
        int start = 0;
        int end = content.length();

        while (start < end && content.charAt(start) < 33) start++;
        while (end > start && content.charAt(end - 1) < 33) end--;

        return content.substring(start, end);
    }
}