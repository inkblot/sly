sly
=
![sly](https://img.shields.io/maven-central/v/org.movealong/sly.svg)

It's not hard to write pure functional code in Java. Just avoid mutability,
avoid side effects, and focus on writing expressions instead of statements.
This might seem like a lot to sacrifice in a language with a long history of
procedural object-oriented programming, but all of the necessary language
features to write pure functional code exist in Java and the experience is
actually quite enjoyable. The main stumbling block to adopting this style is
the lack of framework and feature libraries that respect these same
conventions.

`sly` is a functional framework built using
[lambda](https://github.com/palatable/lambda). The core principle of its
design is to express common application needs in a type-safe pure functional
style, and in discrete units that can be included as necessary.

## Feature Areas

`sly` is organized into modules that implement discrete feature areas. Some
feature areas are the bedrock type-safe functional concepts that just need a
generic implementation and are not already satisfied by the
[palatable](https://github.com/palatable) libraries. The rest are the kinds
of generic feature functionality that typically make up a framework library.

## Contributing

`sly` is a work in progress, and I am having a lot of fun making progress. For
now, I'd like to write the code myself and at my own pace. Please submit
feature requests, bug reports, and questions at
https://github.com/inkblot/sly/issues.
