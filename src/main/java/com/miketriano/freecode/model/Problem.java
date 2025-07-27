package com.miketriano.freecode.model;

import java.util.List;

import lombok.Getter;

@Getter
public class Problem {
    private String id;
    private String name;
    private Difficulty difficulty;
    private List<String> description;
    private String template;
    private List<TestCase> testCases;
}
