# sly-app

Applications are typically composed of discrete pieces of code that each
implement a separate part of the application. At startup, the pieces are
assembled to create the application before it is then run. This module
implements a framework for assembling and running an application.

## Parts of the system

In sly-lang, these separate pieces are ***services***. A service can use
***handles*** to reference other services as ***dependencies***. Services are
***bound*** to handles in order to assemble them into an ***application***. An
application and its dependencies are ***resolved*** in order to then ***run***
the application.

### Services

The service objects that make up an application are each encapsulated in the
`Service` monad, whose monadic effect is the resolution of a service object and
its dependencies. The `Service` monad expresses dependencies via handles that
reference them, and a function that can instantiate the service object within
an `IO` effect, given those dependencies. A `Service` monad that expresses no
dependencies is the ***pure*** form of the monad.

Pure `Service` monads can also express the `IO` monad in different ways to
produce singleton and on-demand service objects.

#### Singleton

A `Service` that resolves a singleton service object looks like this:

```java
Service<Xyzzy> service = service(io(new Xyzzy()));
```

The service object in this example is a singleton because there is only one
instance of the service type `Xyzzy`, and this is used to initialize a pure
`IO`, and then a `Service`. The pure `IO` is what makes the service object a
singleton.

#### On-demand

A `Service` that resolves an on-demand service object looks like this:

```java
Service<Xyzzy> service = service(io(() -> new Xyzzy()));
```

The service object in this example is instantiated on demand becuase the `IO`
monad expresses construction of the `Xyzzy` class as a deferred operation, in
the thunk that is used to construct the `IO` monad. The effectful `IO` monad is
what makes the service object an on-demand service object.

### Handles

A handle is an instance of the the `ServiceHandle` type. This type has no
meaningful state, and uses reference equality. Construct one like this:

```java
ServiceHandle<Xyzzy> xyzzyHandle = create();
```

### Dependencies

`Service` monads that have dependencies are constructed with the handles of the
dependencies and a function:

```java
ServiceHandle<Zzxyz> zzxyzHandle = create();
Service<Xyzzy> service = service(zzxyzHandle, zzxyz -> io(new Xyzzy(zzxyz)));
```

The function is the `Service` monad's ***resolution function***, and is defered
until the `Service` is resolved. At that time, the function is invoked with the
resolved dependencies. Using the `service` and `service` methods of
`Service`, it is possible to construct services with up to seven dependencies.
Note that all service objects with dependencies are on-demand because the
resolution function itself represents a deferral.

### Binding

Any handle that is used to reference a dependency in another service must be
bound during the construction of the application. Binding looks like this:

```java
ServiceHandle<Xyzzy> xyzzyHandle = create();
Kleisli<App, App, IO<?>, IO<App>> binding =
    bind(xyzzyHandle, service(io(() -> new Xyzzy())));
```

A `sly-lang` application is expressed as a `Kleisli` function known as the
***application function***. Using the `bind` method gives us the first glimpse
of that representation in its return type. This representation allows for
composition, so that multiple bindings look like this:

```java
ServiceHandle<Xyzzy> xyzzyHandle = create();
ServiceHandle<Zzxyz> zzxyzHandle = create();
Kleisli<App, App, IO<?>, IO<App>> bindings =
    bind(xyzzyHandle, service(io(() -> new Xyzzy())))
        .andThen(bind(zzxyzHandle,
                      service(xyzzyHandle,
                              xyzzy -> service(io(new Zzxyz(xyzzy))))));
```

The application function is built up in this way from as many bindings as are
needed. The order in which bindings occur is not semantically important, even
in respect of dependencies, but a convention of binding each service *after*
the services it depends on will keep the application orderly.

### Application

The application produced by the application function and then run is a service
object that implements the `Application` interface. This is a functional
interface that defines a single method, which is meant to produce the the
application's result, wrapped in an arbitrary `Functor`. The intended use for
this method is that it would produce a value to be returned by a program
entrypoint, such as a `main` method.

### Resolution

When thee `Application` service is resolved, it causes the resolution of all
dependency services in a single recursive operation. Resolution is a step that
is composed with the bindings to complete the application function:

```java
ServiceHandle<Xyzzy> xyzzyHandle = create();
ServiceHandle<Zzxyz> zzxyzHandle = create();
Kleisli<App, WxzyApplication, IO<?>, IO<WxzyApplication>> bindings =
    bind(xyzzyHandle, service(io(() -> new Xyzzy())))
        .andThen(bind(zzxyzHandle,
                      service(xyzzyHandle,
                              xyzzy -> service(io(new Zzxyz(xyzzy))))))
        .andThen(resolve(service(zzxyzHandle,
                                 zzxyz -> io(new WxyzApplication(zzxyz)))));
```

Because the operation is recursive, services and their dependencies must form a
directed graph. No series of dependencies may create a cycle. For example, if
`A` depends on `B`, then `B` may not depend on `A`. This prohibition applies
across transitive dependencies as well. Cycles are detected at resolution time
and result in a thrown exception.

### Running

Running an application is a simple matter of passing the application function
to the `run` method:

```java
ServiceHandle<Xyzzy> xyzzyHandle = create();
ServiceHandle<Zzxyz> zzxyzHandle = create();
IO<Integer> result =
    run(bind(xyzzyHandle, service(io(() -> new Xyzzy())))
            .andThen(bind(zzxyzHandle,
                          service(xyzzyHandle,
                                  xyzzy -> service(io(new Zzxyz(xyzzy))))))
            .andThen(resolve(service(zzxyzHandle,
                                     zzxyz -> io(new WxyzApplication(zzxyz))))));
```

This example assumes that the `WxyzApplication` class implements the interface
`Application<Integer, IO<?>, IO<Integer>>`. The third type parameter to this
interface is what determines the type that `run` returns.

## A complete `main` method

Putting together all of the code fragments above, the resulting `main` method
might look like this:

```java
public static int main(String[] argv) {
    ServiceHandle<Xyzzy>           xyzzyHandle = create();
    ServiceHandle<Zzxyz>           zzxyzHandle = create();
    ServiceHandle<WxyzApplication> wxyzHandle  = create();
    return performingIO()
        .andThen(throwingExceptions())
        .apply(run(bind(xyzzyHandle, service(io(() -> new Xyzzy())))
                       .andThen(bind(zzxyzHandle,
                                     service(xyzzyHandle,
                                             xyzzy -> service(io(new Zzxyz(xyzzy))))))
                       .andThen(bind(wxyzHandle,
                                     service(zzxyzHandle,
                                             zzxyz -> service(io(new WxyzApplication(zzxyz))))))
                       .andThen(resolve(wxyzHandle))));
}
```

Like the samples above, this example assumes that `WxyzApplication` implements
`Application<Integer, IO<?>, IO<Integer>>`. The overall structure of this
application is a composition of three services. `WxyzApplication` is the "main"
service, and it depends on `Zzxyz`. `Zzxyz` in turn depends on `Xyzzy`. There
are no assumptions in this example about the methods of the `Xyzzy` or `Zzxyz`,
or any of their supertypes.