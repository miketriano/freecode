package com.miketriano.freecode.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CodeExecutor {
    private final ThreadPoolExecutor executorService;

    private static final String IMAGE_NAME = "python-sandbox";
    private static final String MEMORY_LIMIT = "128m";
    private static final String CPU_LIMIT = "0.1";
    private static final String PIDS_LIMIT = "10";
    private static final String TMP_DIRECTORY = "/tmp";
    private static final String CID_DIRECTORY = "/var/lib/freecode/cids";
    private static final String CONTAINER_SCRIPT_DIRECTORY = "/app/scripts";
    
    private static final int TIMEOUT_SECONDS = 3;
    private static final int MAX_CONTAINERS = 5;

    public CodeExecutor() {
        this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_CONTAINERS);
    }

    public CompletableFuture<String> run(final String pythonCode) {
        System.out.println("Running docker on new thread. Queue size: " + executorService.getQueue().size());
        return CompletableFuture.supplyAsync(() -> runDocker(pythonCode), executorService);
    }

    private String runDocker(final String pythonCode) {
        final String pythonFile = UUID.randomUUID() + ".py";
        final String cidFile = UUID.randomUUID() + ".cid";
        final String[] command = new String[] {
                "docker",
                "run",
                "--rm",
                "--network", "none",
                "--read-only",
                "--memory", MEMORY_LIMIT,
                "--memory-swap", MEMORY_LIMIT,
                "--cpus", CPU_LIMIT,
                "--pids-limit", PIDS_LIMIT,
                "--cidfile", CID_DIRECTORY + "/" + cidFile,
                "-v", TMP_DIRECTORY + ":" + CONTAINER_SCRIPT_DIRECTORY + ":ro",
                IMAGE_NAME,
                CONTAINER_SCRIPT_DIRECTORY + "/" + pythonFile
        };

        Path dockerCidPath = Paths.get(CID_DIRECTORY, cidFile);
        Path pythonPath = Paths.get(TMP_DIRECTORY, pythonFile);
        Process process = null;
        boolean finished = false;

        try {
            Files.writeString(pythonPath, pythonCode);

            process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                // The code timed out so we have to forcibly kill the process and container
                killContainer(process, dockerCidPath);
                return "Timeout limit reached";
            }

            final InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(),
                    StandardCharsets.UTF_8);
            final BufferedReader reader = new BufferedReader(inputStreamReader);

            // Encountered a runtime error so return entire output
            if (process.exitValue() != 0) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            // Return first line which should be the result
            return String.valueOf(reader.readLine()).trim();
        } catch (final IOException | InterruptedException e) {
            System.out.println("Error running docker command");
            e.printStackTrace();
            return "Internal error";
        } finally {
            cleanup(dockerCidPath, pythonPath);
        }
    }

    private void killContainer(final Process process, final Path dockerCid) {
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
        }

        if (Files.exists(dockerCid)) {
            try {
                final String containerId = Files.readString(dockerCid).trim();
                System.out.println("Destroying container with id " + containerId);
                final Process killProcess = new ProcessBuilder("docker", "kill", containerId)
                        .redirectErrorStream(true)
                        .start();
                killProcess.waitFor(5, TimeUnit.SECONDS);
            } catch (final IOException | InterruptedException e) {
                System.out.println("Error killing docker container");
                e.printStackTrace();
            }
        }
    }

    private void cleanup(final Path dockerCidFile, final Path pythonFile) {
        try {
            Files.deleteIfExists(dockerCidFile);
        } catch (final IOException e) {
            System.out.println("Error deleting file");
            e.printStackTrace();
        }

        try {
            Files.deleteIfExists(pythonFile);
        } catch (final IOException e) {
            System.out.println("Error deleting file");
            e.printStackTrace();
        }
    }
}
