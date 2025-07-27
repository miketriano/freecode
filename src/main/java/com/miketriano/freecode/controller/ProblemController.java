package com.miketriano.freecode.controller;

import com.miketriano.freecode.model.Problem;
import com.miketriano.freecode.util.ProblemProvider;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class ProblemController {

    private ProblemProvider problemProvider;

    public ProblemController(final ProblemProvider problemProvider) {
        this.problemProvider = problemProvider;
    }

    public void getAll(final Context ctx) {
        ctx.json(problemProvider.getAll());
    }

    public void getOne(final Context ctx) {
        final String id = ctx.pathParam("id");
        final Problem problem = problemProvider.getById(id);

        if (problem != null) {
            ctx.json(problem);
        } else {
            ctx.status(HttpStatus.BAD_REQUEST);
        }
    }
}
