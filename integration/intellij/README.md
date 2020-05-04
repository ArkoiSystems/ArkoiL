## Welcome to ArkoiIntegration

This is the official plugin for the Arkoi language, which is necessary for fast and easy development with it. It supports basic functionalities like syntax and semantic error highlighting or file creation.

Due to the compiler integration, everything behaves the same as when compiling the source code. This creates a deep binding to the compiler, so there is no difference in your code routine.

### Features

 - Syntax highlighting
 - Folding/Commenter/Brace Matcher
 - Creation of files (enums, classes etc.)
 - Compiler integration for semantic analysis etc.

### Examples

Here is an example for a project:
##### src/main.ark:
```
import "test" as test {
  select sampleAnnotation as sampleAnnotation
}

@sampleAnnotation
fun main<int>() {
  println(test.hello_world())
  return 0
}
```

##### src/test.ark:
```
annotation sampleAnnotation

fun hello_world<string>() = "Hello World"
```

### Support or Contact

Do you have problems with the plugin or even with the compiler? Contact me via [єхcsє#5543](https://discordapp.com/) or report a problem.
