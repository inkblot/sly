# sly-app

Applications are typically composed of discrete pieces of code that each
implement a separate part of the application. At startup, the pieces are
assembled to create the application before it is then run. **sly-app**
implements a framework for assembling and running an application.

Broadly, there are two types of applications. There are *short-lived*
applications that are run with a set of parameters, then do some work dictated
by those parameters, and finally produce a result before terminating. There are
*long-lived* applications that start and then run continuously, doing work
in response to input from the environment until they are instructed to stop.
**sly-app** supports both short-lived and long-lived applications.

## Parts of the system

In **sly-app**, these separate pieces are *services*. A service uses *handles*
to reference other services as *dependencies*. Services are *bound* to handles
in order to make them available as dependencies. An application is either a
`Runner` that implements a short-lived application, or a `Starter` that
implements a long-lived application. An *application function* is the sequence
of bindings followed by a *resolution* that results in one of these types. The
application function that produces a `Runner` is then either *run*, or one that
produces a `Starter` is *started*, in order to commence the execution of the
application.

### Services

The service objects that make up an application are each encapsulated in the
`Service` monad, whose monadic effect is the resolution of a service object by
resolving its dependencies and then using them to construct the service object.
The `Service` monad expresses dependencies via handles, and a function that can
instantiate the service object within an `IO` effect, given those dependencies.
A `Service` monad that expresses no dependencies is the *pure* form of the
monad.

Pure `Service` monads can also express the `IO` monad in different ways to
produce singleton and on-demand service objects.

#### Singleton

A `Service` that resolves a singleton service object looks like this:

```java
Service<Xyzzy> service = service(io(new Xyzzy()));
```

The service object in this example is a singleton because only one instance of
the service type `Xyzzy` is constructed, and then it is used to construct pure
`IO` and `Service` monads. The pure monads are what makes the service object a
singleton.

#### On-demand

A `Service` that resolves an on-demand service object looks like this:

```java
Service<Xyzzy> service = service(io(() -> new Xyzzy()));
```

The service object in this example is instantiated on demand becuase the `IO`
monad expresses construction of the `Xyzzy` class as a deferred operation. The
effectful `IO` monad then invokes the thunk each time it is run, thus
constructing a new instance of `Xyzzy` each time.

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

The function is the `Service` monad's *resolution function*, and is defered
until the `Service` is resolved. At that time, the function is invoked with the
resolved dependencies. Using the `service` methods of `Service`, it is possible
to construct services with up to seven dependencies. Note that all service
objects with dependencies are on-demand because the resolution function itself
represents a deferral.

### Binding

Any handle that is used to reference a dependency in another service must be
bound during the construction of the application. Binding looks like this:

```java
ServiceHandle<Xyzzy> xyzzyHandle = create();
Kleisli<App, App, IO<?>, IO<App>> binding =
    bind(xyzzyHandle, service(io(() -> new Xyzzy())));
```

A `sly-lang` application is expressed as a `Kleisli` function and is known as
the *application function*. Using the `bind` method gives us the first glimpse
of that representation in its return type. This representation as a `Kleisli`
allows for composition, so that multiple bindings look like this:

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
in respect of dependencies, but a convention of binding each handle *before*
the services that reference it will help maintain the application's
readability.

### Resolution

When a service is resolved, it causes the resolution of all its dependencies
in a single recursive operation. Resolution is a step that is composed with the
bindings to produce a complete application function:

```java
ServiceHandle<Xyzzy> xyzzyHandle = create();
ServiceHandle<Zzxyz> zzxyzHandle = create();
Kleisli<App, WxyzRunner, IO<?>, IO<WxyzRunner>> bindings =
    bind(xyzzyHandle, service(io(() -> new Xyzzy())))
        .andThen(bind(zzxyzHandle,
                      service(xyzzyHandle,
                              xyzzy -> service(io(new Zzxyz(xyzzy))))))
        .andThen(resolve(service(zzxyzHandle,
                                 zzxyz -> io(new WxyzRunner(zzxyz)))));
```

Because the operation is recursive, services and their dependencies must form a
directed graph. No series of dependencies may create a cycle. For example, if
`A` depends on `B`, then `B` may not depend on `A`. This prohibition applies
across transitive dependencies as well. Cycles are detected at resolution time
and result in a thrown exception.

### Running

A short-lived application uses the `run` method to run the application. To run
as a short-lived application, the application function must resolve a service
object that implements the `Runner` interface. This is a functional interface
that defines a single method, which is meant to produce the application's
result, wrapped in an arbitrary `Functor`. This `Functor`-wrapped value is then
returned from `run`. The intended use for the `run` method is that the value
that remains after interpreting the `Functor` is the value returned from a
program entrypoint, such as the `int` returned by a `main` method.

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

This example assumes that the `WxyzRunner` class implements the interface
`Runner<Integer, IO<?>, IO<Integer>>`. The third type parameter to this
interface is what determines the type that `run` returns.

### Starting

A long-lived application uses the `start` method to run the application. To run
a long-lived application, the application function must resolve a `Starter`.
This class is part of a mechanism that defines lifecycle boundaries for service
objects that have distinct setup and tear down phases, and offers useful
functionality during the interval in between, e.g. a listening socket or an
event subscriber. The `start` method will return after invoking the `Starter`,
and in order for a long-lived application to continue running it is important
that the `Starter` creates and starts at least one non-daemon `Thread`.

## A complete short-lived `main` method

Putting together all the code fragments above, the resulting `main` method
for a short-lived might look like this:

```java
public static int main(String[] argv) {
    ServiceHandle<Xyzzy>      xyzzyHandle = create();
    ServiceHandle<Zzxyz>      zzxyzHandle = create();
    ServiceHandle<WxyzRunner> wxyzHandle  = create();
    return performingIO()
        .andThen(throwingExceptions())
        .apply(run(bind(xyzzyHandle, service(io(() -> new Xyzzy())))
                       .andThen(bind(zzxyzHandle,
                                     service(xyzzyHandle,
                                             xyzzy -> service(io(new Zzxyz(xyzzy))))))
                       .andThen(bind(wxyzHandle,
                                     service(zzxyzHandle,
                                             zzxyz -> service(io(new WxyzRunner(zzxyz))))))
                       .andThen(resolve(wxyzHandle))));
}
```

This example assumes that `WxyzRunner` implements
`Runner<Integer, IO<?>, IO<Integer>>`. The overall structure of this
application is a composition of three services. `WxyzRunner` is the "main"
service, and it depends on `Zzxyz`. `Zzxyz` in turn depends on `Xyzzy`. There
are no assumptions in this example about the methods of the `Xyzzy` or `Zzxyz`,
or any of their supertypes and these service object can supply functionality as
needed.

## A complete long-lived `main` method

Putting together the fragments above a different way, the resulting `main`,
method for a long-lived might look like this:

```java
public static int main(String[] argv) {
    ServiceHandle<Xyzzy>       xyzzyHandle   = create();
    ServiceHandle<Zzxyz>       zzxyzHandle   = create();
    ServiceHandle<WxyzStarter> starterHandle = create();
    return performingIO()
        .andThen(throwingExceptions())
        .apply(run(bind(xyzzyHandle, service(io(() -> new Xyzzy())))
                       .andThen(bind(zzxyzHandle,
                                     service(xyzzyHandle,
                                             xyzzy -> service(io(new Zzxyz(xyzzy))))))
                       .andThen(bind(starterHandle,
                                     service(zzxyzHandle,
                                             zzxyz -> service(io(new WxyzStarter(zzxyz))))))
                       .andThen(resolve(wxyzHandle))));
}
```

This example assumes that `WxyzStarter` implements `Starter<IO<?>`. The overall
structure of this application is otherwise the same as the short-lived example.