package com.miketriano.freecode;

import com.miketriano.freecode.controller.ProblemController;
import com.miketriano.freecode.controller.SubmitController;
import com.miketriano.freecode.util.CodeExecutor;
import com.miketriano.freecode.util.ProblemProvider;

import io.javalin.Javalin;
import io.javalin.vue.VueComponent;

public class App {
    public static void main(String[] args) {
        final int PORT = 8080;

        final Javalin app = Javalin.create(config -> {
            config.staticFiles.enableWebjars();
            config.vue.vueInstanceNameInJs = "app";
            config.router.contextPath = "/freecode";
            config.http.maxRequestSize = 1000L;
        }).start(PORT);

        app.get("/", new VueComponent("dashboard-view"));
        app.get("/problems/{id}", new VueComponent("problem-view"));

        final ProblemProvider problemProvider = new ProblemProvider();
        final CodeExecutor codeExecutor = new CodeExecutor();

        final ProblemController problemController = new ProblemController(problemProvider);
        app.get("/api/problems", problemController::getAll);
        app.get("/api/problems/{id}", problemController::getOne);

        final SubmitController submitController = new SubmitController(problemProvider, codeExecutor);
        app.post("/api/submit/{id}", submitController::submit);

        app.get("/editor", new VueComponent("editor-view"));
    }
}
