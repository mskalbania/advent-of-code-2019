package edu.advent.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class InputLoader {

    public static List<String> getInputAsStrings(String name) {
        try {
            Path path = Paths.get(InputLoader.class.getResource("/" + name + ".txt").toURI());
            return Files.readAllLines(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static long[] getInputAsNumbers(String name) {
        return getInputAsStrings(name).stream()
                                      .mapToLong(Long::parseLong)
                                      .toArray();
    }
}
