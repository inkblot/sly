# Change log

---
Important changes in each release of `sly` will be noted in this file.

## [Unreleased]

### Added

Added `HyperFn` in `sly-lang` and these implementations:

- `RunningEitherT`: EitherT<M, L, R> -> MonadRec<Either<L, R>, M>
- `RunningMaybe`: MaybeT<M, A> -> MonadRec<Maybe<A>, M>
- `RunningStateT`: StateT<S, M, A> -> MonadRec<Tuple2<A, S>, M>

Added matchers in `sly-lambda-matchers`:

- `Tuple2Matcher`: A matcher of `Tuple2`

## [0.1.1]

### Added

In `sly-lang`, these implementations of `NaturalTransformation` are new:

- `PerformingIO`: IO -> Try
- `MappingStreamT`: StreamT<M, ?> -> StreamT<N, ?>
- `MappingMaybeT`: MaybeT<M, ?> -> MaybeT<N, ?>
- `MappingEitherT`: EitherT<M, L, ?> -> EitherT<N, L, ?>
- `MappingStateT`: StateT<S, M, ?> -> StateT<S, N, ?>
- `MappingIterateT`: IterateT<M, ?> -> IterateT<N, ?>
- `MappingReaderT`: ReaderT<R, M, ?> -> ReaderT<R, N, ?>
- `MappingSafeT`: SafeT<M, ?> -> SafeT<N, ?>
- `MappingIdentityT`: IdentityT<M, ?> -> IdentityT<N, ?>
- `RunningReaderT`: ReaderT<R, M, ?> -> M
- `RunningSafeT`: SafeT<M, ?> -> M
- `RunningIdentityT`: IdentityT<M, ?> -> M
- `TransformingMaybe`: Maybe<?> -> MaybeT<M, ?>
- `TransformingEither`: Either<L, ?> -> EitherT<M, L, ?>
- `TransformingState`: State<S, ?> -> StateT<S, M, ?>
- `ThrowingExceptions`: Try -> Identity

Also in `sly-lang`, these functions are new:

- `FromPure`: constructs a `NaturalTransformation` from a `Pure`
- `ToFn`: allows a `NaturalTransformation` to be used as an `Fn1`

In `sly-jdk`, these types are new:

- `Stringy`: An implementation of both `CharSequence` and `Iterable<Character>` which makes these and `String` fungible.

In `sly-hamcrest`, these helper classes are new:

- `DescriptionOf`: Extracts a description from a `SelfDescribing`
- `MismatchDescription`: Extracts a mismatch description from a `Matcher` and a test subject
- `IndentingDescription`: Decorates a `Description` and causes all lines written to the decorated description to be
  indented with the supplied prefix.

In `sly-jdk-matchers`, these matchers are new:

- `IterableMatcher` added to new module `sly-jdk-matchers`

In `sly-lambda-matchers`, these matchers are new:

- `TryMatcher`: A matcher of `Try`
- `StreamTMatcher`: A matcher of `StreamT`
- `JustMatcher`: A matcher of `Maybe`

### Changed

- Converted the `sly` module into an aggregator
- Moved existing `org.movealong.sly.lang` package into `sly-lang`
- Added `sly-hamcrest`, `sly-jdk`, `sly-jdk-matchers`, and `sly-lambda-matchers` submodules
- Updated parent pom to 0.0.34

## [0.0.3]

### Changed

- Updated parent pom to 0.0.31

## [0.0.2]

### Added
- `Match`, `Traverse`, `SequenceM`, and `SafeSequenceM` functions
- `DefaultValueLens` and `MapLens` lenses

## [0.0.1]

### Added
- Maven module, license, and CI pipeline
