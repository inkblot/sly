/*
 * Copyright (c) 2024 Nate Riffe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.movealong.sly.app;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hmap.HMap;
import com.jnape.palatable.lambda.functions.specialized.Kleisli;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.optics.Lens;
import com.jnape.palatable.shoki.impl.StrictStack;
import lombok.RequiredArgsConstructor;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.hmap.HMap.emptyHMap;
import static com.jnape.palatable.lambda.functions.Fn0.fn0;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Eq.eq;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Find.find;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.io.IO.throwing;
import static com.jnape.palatable.lambda.optics.functions.Set.set;
import static com.jnape.palatable.lambda.optics.functions.View.view;
import static com.jnape.palatable.lambda.optics.lenses.HMapLens.valueAt;
import static com.jnape.palatable.shoki.impl.StrictStack.strictStack;
import static lombok.AccessLevel.PRIVATE;
import static org.movealong.sly.app.Service.serviceRef;
import static org.movealong.sly.lang.nt.PerformingIO.performingIO;
import static org.movealong.sly.lang.nt.ThrowingExceptions.throwingExceptions;

/**
 * A
 *
 * @see Service
 * @see ServiceHandle
 */
@RequiredArgsConstructor(access = PRIVATE)
public final class App {
    private static final App  INSTANCE = new App(emptyHMap());
    private final        HMap registry;

    /**
     * Binds a {@link Service} to a {@link ServiceHandle}. Binding a service to
     * a handle makes the service accessible to other services via resolution.
     * A handle can only be bound once, and rebinding a handle will result in
     * an exception. The service that the supplied handle is bound  to it may
     * not resolve other handles that are bound to services that resolve the
     * supplied handle, including indirectly. This will not cause an error
     * immediately, but will if any handle in the cycle is later resolved.
     *
     * @param <S>     the service type
     * @param handle  the {@link ServiceHandle} to bind the service to
     * @param service the {@link Service} being bound
     * @return A {@link Kleisli} function that forms part of the composition of
     * an application
     */
    public static <S> Kleisli<App, App, IO<?>, IO<App>> bind(ServiceHandle<S> handle, Service<S> service) {
        Lens.Simple<HMap, Maybe<Service<S>>> lens = valueAt(handle);
        return app -> view(lens, app.registry).match(
            fn0(() -> io(new App(set(lens, just(service), app.registry)))),
            s -> throwing(new ServiceException("Binding is a duplicate", handle)));
    }

    /**
     * Resolves an {@link Application}. Composing the result of a call to
     * <code>resolve</code> onto a {@link Kleisli} composed of one or more
     * <code>bind</code> calls will produce an <i>application function</i>
     * suitable for use with <code>run</code>.
     *
     * @param <R>     carrier type of the application return
     * @param <F>     {@link Functor} type of the application return
     * @param <FR>    the application return type
     * @param <A>     the {@link Application} type
     * @param service the {@link Service}
     * @return An <i>application function</i>
     */
    public static <R, F extends Functor<?, F>, FR extends Functor<R, F>, A extends Application<R, F, FR>>
    Kleisli<App, A, IO<?>, IO<A>> resolve(Service<A> service) {
        return app -> service.resolveService(app.new AppServices(strictStack()));
    }

    /**
     * Resolves an {@link Application} by its {@link ServiceHandle}. The handle
     * must have been previously bound using <code>bind</code> to a
     * {@link Service} that has a carrier of type <code>A</code>. Composing the
     * result of a call to <code>resolve</code> onto a {@link Kleisli} composed
     * of one or more <code>bind</code> calls will produce an <i>application
     * function</i> suitable for use with <code>run</code>.
     *
     * @param <R>    carrier type of the application return
     * @param <F>    {@link Functor} type of the application return
     * @param <FR>   the application return type
     * @param <A>    the {@link Application} type
     * @param handle handle for the application service
     * @return An <i>application function</i>
     */
    public static <R, F extends Functor<?, F>, FR extends Functor<R, F>, A extends Application<R, F, FR>>
    Kleisli<App, A, IO<?>, IO<A>> resolve(ServiceHandle<A> handle) {
        return resolve(serviceRef(handle));
    }

    /**
     * Runs an application in the form of an <i>application function</i>,
     * producing a {@link Functor} that bears the ultimate return type.
     * <code>run</code> will perform the {@link IO} that results from applying
     * the function exactly once, throw an exception if that {@link IO} is in
     * its error mode, and otherwise return the {@link IO}'s result.
     *
     * @param <R>         carrier type of the application return
     * @param <F>         {@link Functor} type of the application return
     * @param <FR>        the application return type
     * @param <A>         the {@link Application} type
     * @param application the {@link Kleisli} function representing the application
     * @return the result of running the application
     */
    public static <R, F extends Functor<?, F>, FR extends Functor<R, F>, A extends Application<R, F, FR>>
    FR run(Kleisli<App, A, IO<?>, IO<A>> application) {
        return performingIO()
            .andThen(throwingExceptions())
            .<A, Identity<A>>apply(application.apply(INSTANCE))
            .runIdentity()
            .run();
    }

    @RequiredArgsConstructor(access = PRIVATE)
    private final class AppServices implements Services {
        private final StrictStack<ServiceHandle<?>> resolving;

        @Override
        public <S> IO<S> resolve(ServiceHandle<S> handle) {
            return find(eq(handle), resolving)
                .<IO<S>>match(
                    fn0(() -> view(valueAt(handle), registry).match(
                        fn0(() -> throwing(new ServiceException("Binding missing", handle))),
                        s -> s.resolveService(new AppServices(resolving.cons(handle))))),
                    dupe -> throwing(new ServiceException("Dependency cycle detected", handle)))
                .coerce();
        }
    }
}