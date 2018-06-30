## General
- Encoding: UTF-8
- Line ending: LF (Unix)
- Line length: 100 characters
- Indentation: 2 spaces

## Scala
Tuubes uses [scalafmt](https://scalameta.org/scalafmt/) to check and format the code. We mostly follow the [official Scala style guide](https://docs.scala-lang.org/style/).

To run **scalafmt**, open a Terminal in the `TuubesCore` directory and run:
1. `./gradlew checkScalafmt` to check the code
2. `./gradlew scalafmt` to reformat the code

## Java
Scalafmt doesn't work with Java source files, please format your code carefully, with the [Google's Java style](https://google.github.io/styleguide/javaguide.html).

## Config files
- [scalafmt configuration](.scalafmt.conf)
- [editorconfig file](.editorconfig)
- [IntelliJ formatter settings](CodeStyle.xml)

The scalafmt configuration takes over everything else and scalafmt should be the primary formatting tool. The other files are provided to help you but they don't reflect the exact code style.