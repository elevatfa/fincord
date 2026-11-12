# Architecture

- Keep Android sources at repository root and build them directly in GitHub Actions; archives are not the source of truth.
- Target Java and Kotlin bytecode at Java 17 together; Android Gradle compilation validates their compatibility.
