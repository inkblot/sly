# Change log

---
Important changes in each release of `sly` will be noted in this file.

## [0.4.0](https://github.com/inkblot/sly/compare/sly-v0.3.1-SNAPSHOT...sly-v0.4.0) (2026-03-07)


### Features

* Add SemanticError and rewrite docs ([4fc9016](https://github.com/inkblot/sly/commit/4fc90165708977d14d6b71cd1034920bd2390813))
* auto-fix parent versions in multi-module release PRs ([0c2ccf3](https://github.com/inkblot/sly/commit/0c2ccf39caa2a08553531d7902f141c30aa2debc))
* create separate workflow to fix multi-module release PRs ([ceb0481](https://github.com/inkblot/sly/commit/ceb04815ead12760f423aaeaed848d1a91f0088a))
* Implement a Failures model type ([7f8c52a](https://github.com/inkblot/sly/commit/7f8c52ad0256c72863ae77893080977cb0516ee7))
* Implement matchers for all kinds of failures ([e797db2](https://github.com/inkblot/sly/commit/e797db291ea5d39a750eba70a1b740a01685f8cc))
* Implement ThrowableCauseMatcher ([49da32b](https://github.com/inkblot/sly/commit/49da32b2b59c3d0673a652f35336c63e49a004d4))
* Implement ThrowableEventuallyMatcher ([3a09cf5](https://github.com/inkblot/sly/commit/3a09cf5ef1fd419e9a56857ba06af41aefbdfb5a))
* Reimplement hamcrest's ThrowableMessageMatcher ([74d425d](https://github.com/inkblot/sly/commit/74d425dd3215df82d6b19944b25509b99a6e8db8))


### Bug Fixes

* Add missing Javadoc ([468635c](https://github.com/inkblot/sly/commit/468635cffe74a0f7aa928eabb76a0a69efb326a3))
* check branch name instead of PR author ([c97237e](https://github.com/inkblot/sly/commit/c97237ebfdf5ec21a5b13293891dfb08eef75e37))
* Improve flexibility of IterableMatcher ([9a7fc6c](https://github.com/inkblot/sly/commit/9a7fc6cc5a28b34e17958dda9a7a161a91e5d938))
* remove redundant version tags from child modules ([7f423c3](https://github.com/inkblot/sly/commit/7f423c3f163aaf22bac7ff3d5932b39e79f7b9bc))
* set manifest to current SNAPSHOT version ([a42aaba](https://github.com/inkblot/sly/commit/a42aabae68085ce3a8b333111160c96ac865c895))

## [Unreleased]

### Added

- Added `ConcurrentlyPerformingIO` to `sly-lang`
- Added `Atom` and `Caller` to `sly-jdk`

## [0.3.0]

### Updated

Updated `movealong-oss` to version `0.0.36`

### Added

Added the [`sly-app`](app/README.md) module with a functional framework for
building an application. The initial module includes these public types:

- `App`
- `Service`
- `ServiceHandle`
- `ServiceException`
- `Runner`
- `Starter`
- `Stopper`

Added these `HyperFn` implementations in `sly-lang`

- `Fmap`
- `FlatMap`
- `TrampolineM`
- `CatchError`
- `Traverse`

## [0.2.1]

Fix a pom issue. Deployment requires a `name` element in the maven modules.

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
