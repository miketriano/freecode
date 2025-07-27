package com.miketriano.freecode.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.miketriano.freecode.model.Problem;
import com.miketriano.freecode.model.TestCase;
import com.miketriano.freecode.util.CodeExecutor;
import com.miketriano.freecode.util.ProblemProvider;

import io.javalin.http.Context;

public class SubmitController {

    private final ProblemProvider problemProvider;
    private final CodeExecutor codeExecutor;

    public SubmitController(final ProblemProvider problemProvider, final CodeExecutor codeExecutor) {
        this.problemProvider = problemProvider;
        this.codeExecutor = codeExecutor;
    }

    public void submit(final Context ctx) {
        final String id = ctx.pathParam("id");
        final Problem problem = problemProvider.getById(id);
        final Map<TestCase, CompletableFuture<String>> futureMap = new LinkedHashMap<>();

        System.out.println("Executing test cases for " + problem.getName());

        for (final TestCase testCase : problem.getTestCases()) {
            final String pythonCode = formatCode(ctx.body(), testCase.getInput());
            final CompletableFuture<String> future = codeExecutor.run(pythonCode);
            futureMap.put(testCase, future);
        }

        final List<TestCase> results = new ArrayList<>();
        for (final TestCase testCase : futureMap.keySet()) {
            final String result = getResult(futureMap.get(testCase));
            final TestCase testCaseResult = testCase.toBuilder()
                    .result(result)
                    .build();
            results.add(testCaseResult);
        }

        ctx.json(results);
    }

    private String formatCode(final String usersCode, final String input) {
        return new StringBuilder(usersCode).append(System.lineSeparator())
                .append("res = Solution().").append(input).append(System.lineSeparator())
                .append("print(res)")
                .toString();
    }

    private String getResult(final CompletableFuture<String> future) {
        try {
            return future.get();
        } catch (final InterruptedException | ExecutionException e) {
            System.out.println("Error geting result from CodeExecutor");
            e.printStackTrace();
            return "Internal server error";
        }
    }
}
