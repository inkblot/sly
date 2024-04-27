sly
=
![sly](https://img.shields.io/maven-central/v/org.movealong/sly.svg)

It's not hard to write pure functional code in Java. Just avoid mutability,
avoid side effects, and focus on writing expressions instead of statements.
This might seem like a lot to sacrifice in a language with a long history of
procedural object-oriented programming, but all of the necessary language
features to write pure functional code exist in Java and the experience is
actually quite enjoyable.

`sly` is a functional framework built using
[lambda](https://github.com/palatable/lambda). The core principle of its
design is to express common application needs in a type-safe pure functional
style, and in discrete units that can be included as necessary.

## Feature Areas

`sly` is organized into modules that implement discrete feature areas:

* [`sly-app`](app/README.md): An application wiring framework
* `sly-jdk`: Utilities for working with JDK types
* `sly-lang`: Functions and `HyperFn`s for use in building algorithms
* `sly-model`: Basic value types for modelling common application concerns

Additionally, `sly` includes these modules to aid in testing:

* `sly-hamcrest`: Utilities for working with hamcrest
* `sly-jdk-matchers`: Matchers for JDK types
* `sly-lambda-matchers`: Matchers for lambda and other palatable types
* `sly-model-test`: Matchers and fixtures for value types in `sly-model`

## Contributing

`sly` is a work in progress, and I am having a lot of fun making progress. For
now, I'd like to write the code myself and at my own pace. Please submit
feature requests, bug reports, and questions at
https://github.com/inkblot/sly/issues.
