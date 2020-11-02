# Arkoi Language
![GitHub repo size](https://img.shields.io/github/repo-size/ArkoiSystems/ArkoiL)
![GitHub license](https://img.shields.io/github/license/ArkoiSystems/ArkoiL)
![GitHub commit activity](https://img.shields.io/github/commit-activity/w/ArkoiSystems/ArkoiL)

Arkoi is a compiled programming language that tries to combine properties of many
languages. The focus is on creating a simple, yet powerful programming language that
is also quite customizable.

### Compiler structure:
![compiler structure](assets/structure.png "General structure of the Compiler")

## Ideas behind the language:
* Strong type system
* Flexible code through features
* Understandable code
* Combining VM-based features (e.g. Java, Python) with compiled code

## Contributing

If you are interested in developing ArkoiL, please make sure that you read the
documentation and are familiar with the structure of the project. To give some
information about the structure I have created a short summary:

* ``compiler/``     - the compilers directory.
    * ``cmake/``    - cmake files to search relevant libraries.
    * ``src/``    - the sourcecode of the compiler.
    * ``CMakeLists.txt`` - the cmake file to build the project etc.
* ``tests/``         - examples which you can use to test the language.
* ``stdlib/``        - functions and structs which are directly included to the language.

## License 

All the subprojects of this repository are licensed under the Apache 2.0 license. You're
explicitly permitted to develop commericial applications using ArkoiL.

You can read more about the license [here](http://www.apache.org/licenses/LICENSE-2.0).

