package by.it.group410971.petrenko.lesson15;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceScannerC {

    private static final Pattern CLASS_PATTERN =
            Pattern.compile("\\bclass\\s+(\\w+)");

    private static final Pattern USE_PATTERN =
            Pattern.compile("\\bnew\\s+(\\w+)\\b|\\bextends\\s+(\\w+)\\b|\\bimplements\\s+(\\w+)\\b");

    public static void main(String[] args) throws IOException {

        Path root = Paths.get("src");

        // имя класса -> файл
        Map<String, Path> classToFile = new HashMap<>();

        // файл -> используемые классы
        Map<Path, Set<String>> fileUses = new HashMap<>();

        // 1. собираем ВСЕ java-файлы
        Files.walk(root)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    try {
                        String text = Files.readString(p);

                        Matcher cm = CLASS_PATTERN.matcher(text);
                        if (cm.find()) {
                            classToFile.put(cm.group(1), p);
                        }

                        Matcher um = USE_PATTERN.matcher(text);
                        Set<String> uses = new HashSet<>();
                        while (um.find()) {
                            for (int i = 1; i <= 3; i++) {
                                if (um.group(i) != null) {
                                    uses.add(um.group(i));
                                }
                            }
                        }
                        fileUses.put(p, uses);

                    } catch (IOException ignored) {
                    }
                });

        // 2. ищем взаимные зависимости
        Set<String> printed = new HashSet<>();

        for (Path a : fileUses.keySet()) {
            for (Path b : fileUses.keySet()) {
                if (a.equals(b)) continue;

                String classA = getClassName(a);
                String classB = getClassName(b);

                if (classA == null || classB == null) continue;

                boolean aUsesB = fileUses.get(a).contains(classB);
                boolean bUsesA = fileUses.get(b).contains(classA);

                if (aUsesB && bUsesA) {
                    String key = a + "|" + b;
                    String rev = b + "|" + a;
                    if (!printed.contains(key) && !printed.contains(rev)) {
                        System.out.println(rel(a));
                        System.out.println("  " + rel(b));
                        printed.add(key);
                    }
                }
            }
        }
    }

    private static String getClassName(Path p) {
        String name = p.getFileName().toString();
        return name.endsWith(".java")
                ? name.substring(0, name.length() - 5)
                : null;
    }

    private static String rel(Path p) {
        return p.toString()
                .replace("\\", "/")
                .replaceFirst("^src/", "");
    }
}
