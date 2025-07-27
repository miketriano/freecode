# FreeCode

FreeCode is a Java-based coding problem platform that allows users to solve programming challenges in a secure, sandboxed environment. The platform supports problem management, code execution, and test case validation, with a focus on security and extensibility.

## Features
- RESTful API for problem retrieval and code submission
- Secure code execution using a Docker-based Python sandbox
- Problem and test case management via YAML files
- Extensible architecture for adding new problems and languages

## Project Structure
```
├── Dockerfile                # Defines the Python sandbox container
├── startup.sh                # Startup script to build and run the project
├── pom.xml                   # Maven build configuration
├── src/
│   ├── main/
│   │   ├── java/com/miketriano/freecode/
│   │   │   ├── App.java                     # Main application entry point
│   │   │   ├── controller/
│   │   │   │   ├── ProblemController.java   # REST API for problems
│   │   │   │   └── SubmitController.java    # REST API for submissions
│   │   │   ├── model/
│   │   │   │   ├── Difficulty.java          # Enum for problem difficulty
│   │   │   │   ├── Problem.java             # Problem data model
│   │   │   │   └── TestCase.java            # Test case data model
│   │   │   └── util/
│   │   │       ├── CodeExecutor.java        # Handles code execution in Docker
│   │   │       └── ProblemProvider.java     # Loads problems from YAML
│   │   └── resources/
│   │       ├── problem-data/                # YAML files for problems
│   │       └── vue/                         # Frontend templates (Vue)
│   └── test/java/                           # Unit tests
└── target/                                  # Build output
```

## How to Run

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker (for sandboxed code execution)

### Steps
1. **Clone the repository**
2. **Run the startup script:**
   ```bash
   ./startup.sh
   ```
   This will:
   - Build the Docker image for the Python sandbox (if not already built)
   - Build the Java project with Maven (skipping tests)
   - Start the main application

## Security & Attack Mitigation
- **Sandboxed Execution:**
  - User-submitted code is executed inside a minimal Docker container (`python:3.12-slim`) as a non-root user (`sandboxuser`).
  - The container restricts filesystem and process access, preventing code from affecting the host or other users.
- **No Root Access:**
  - The Dockerfile explicitly creates and uses a non-root user for code execution.
- **Resource Isolation:**
  - The container can be further restricted (e.g., with memory and CPU limits) to prevent resource exhaustion attacks.
- **Input Validation:**
  - Problem and test case data are loaded from controlled YAML files, reducing injection risks.
- **No Persistent State:**
  - Each code execution is stateless, and containers are ephemeral.

## Extending the Platform
- Add new problems by creating YAML files in `src/main/resources/problem-data/`.
- Extend controllers or models to support new features or languages.

---
For more details, see the source code and comments in each file.
