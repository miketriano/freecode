package com.miketriano.freecode.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.miketriano.freecode.model.Problem;

public class ProblemProvider {

    private final List<Problem> problemSet = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public ProblemProvider() {
        final Path directory = Paths.get("src/main/resources/problem-data");
        
        try {
            Files.list(directory)
                    .map(this::parseFile)
                    .filter(Objects::nonNull)
                    .forEach(problemSet::add);
        } catch (final IOException e) {
            System.out.println("Error parsing problem data");
            e.printStackTrace();
        }
    }

    public List<Problem> getAll() {
        return problemSet;
    }

    public Problem getById(final String id) {
        return problemSet
                .stream()
                .filter(problem -> problem.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    private Problem parseFile(final Path path) {
        try {
            final String contents = Files.readString(path);
            return objectMapper.readValue(contents, Problem.class);
        } catch (final IOException e) {
            System.out.println("Error parsing file " + path.toAbsolutePath());
            e.printStackTrace();
            return null;
        }
    }
}
