# Change log

---
Important changes in each release of `sly` will be noted in this file.

## [Unreleased]

## [0.2.0]

### Added

Added `HyperFn` in `sly-lang` and these implementations:

- `FixV`: NaturalTransformation<F, G> -> HyperFn<F, A, G, A>
- `IntoEitherT`: MonadRec<Either<L, R>, M> -> EitherT<M, L, R>
- `IntoIdentityT`: MonadRec<Identity<A>, M> -> IdentityT<M, A>
- `IntoIterateT`: MaybeT<M, Tuple2<A, IterateT<M, A>>> -> IterateT<M, A>
- `IntoMaybeT`: MonadRec<Maybe<A>, M> -> MaybeT<M, A>
- `IntoStreamT`: MaybeT<M, Tuple2<Maybe<A>, StreamT<M, A>>> -> StreamT<M, A>
- `JoiningEither`: EitherT<M, L, Either<L, R>> -> EitherT<M, L, R>
- `JoiningIdentity`: IdentityT<M, Identity<A>> -> IdentityT<M, A>
- `JoiningMaybe`: MaybeT<M, Maybe<A>> -> MaybeT<M, A>
- `JoiningState`: StateT<S, M, State<S, A>> -> StateT<S, M, A>
- `RunningEitherT`: EitherT<M, L, R> -> MonadRec<Either<L, R>, M>
- `RunningIdentityT`: IdentityT<M, A> -> MonadRec<Identity<A>, M>
- `RunningIterateT`: IterateT<M, A> -> MaybeT<M, Tuple2<A, IterateT<M, A>>>
- `RunningMaybeT`: MaybeT<M, A> -> MonadRec<Maybe<A>, M>
- `RunningStateT`: StateT<S, M, A> -> MonadRec<Tuple2<A, S>, M>
- `RunningStreamT`: StreamT<M, A> -> MaybeT<M, Tuple2<Maybe<A>, StreamT<M, A>>>

Also added to `sly-lang`:

- `Lifting`: Lift<N> -> NaturalTransformation<M, NM>

Added `sly-model` module with these types:

- `WrappedValue`: a simple unifying interface for tiny types
- `Label`: a wrapped string that represents a label
- `Labeled`: a `Functor` that associates a `Label` with a wrapped value
- `Name`: a wrapped string the represents a name
- `Named`: a `Functor` that associates a `Name` with a wrapped value

Added `sly-model-test` module containing:

- `WrappedValueMatcher`: an abstract superclass for `WrappedValue` matchers
- `LabelMatcher`: A matcher for `Label`
- `NameMatcher`: A matcher for `Name`

Added matchers in `sly-lambda-matchers`:

- `Tuple2Matcher`: A matcher of `Tuple2`

Updated documentation:

- Major update to README
- `IterableMatcher` updated javadoc
- `JustMatcher` updated javadoc
- `StateMatcher` updated javadoc

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
