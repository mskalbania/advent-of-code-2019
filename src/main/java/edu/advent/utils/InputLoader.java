package edu.advent.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

    public static long[] readLinesAsNumbers(String name) {
        return getInputAsStrings(name).stream()
                                      .mapToLong(Long::parseLong)
                                      .toArray();
    }

    public static long[] readComaSeparatedValues(String name) {
        return Arrays.stream(getInputAsStrings(name).get(0).split(","))
                     .mapToLong(Long::parseLong)
                     .toArray();
    }
}
