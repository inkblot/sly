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

import com.jnape.palatable.lambda.functions.*;
import com.jnape.palatable.lambda.functions.builtin.fn3.LiftA2;
import com.jnape.palatable.lambda.functions.builtin.fn4.LiftA3;
import com.jnape.palatable.lambda.functions.builtin.fn5.LiftA4;
import com.jnape.palatable.lambda.functions.builtin.fn6.LiftA5;
import com.jnape.palatable.lambda.functions.builtin.fn7.LiftA6;
import com.jnape.palatable.lambda.functions.builtin.fn8.LiftA7;
import com.jnape.palatable.lambda.functions.recursion.RecursiveResult;
import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.Lazy;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT;
import lombok.RequiredArgsConstructor;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.$.$;
import static com.jnape.palatable.lambda.monad.Monad.join;
import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.readerT;
import static lombok.AccessLevel.PRIVATE;

/**
 * <code>Service</code> is a monad representing the effect of resolving the
 * dependencies of a service object so that it may be constructed.
 * <code>Service</code>s require the use of an {@link App} in order to perform
 * this resolution. A <i>pure</i> <code>Service</code> is one that has no
 * dependencies.
 *
 * @param <S> the service type
 * @see App for more thorough coverage of <code>Service</code> usage.
 */
@RequiredArgsConstructor(access = PRIVATE)
public class Service<S> implements MonadRec<S, Service<?>> {

    private final ReaderT<Services, IO<?>, S> fn;

    IO<S> resolveService(Services services) {
        return fn.runReaderT(services);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <B> Service<B> pure(B b) {
        return new Service<>(fn.pure(b));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <B> Service<B> fmap(Fn1<? super S, ? extends B> fn) {
        return new Service<>(readerT(services -> resolveService(services).fmap(fn)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <B> Service<B> zip(Applicative<Fn1<? super S, ? extends B>, Service<?>> appFn) {
        return new Service<>(readerT(services -> resolveService(services)
            .zip(appFn.<Service<Fn1<? super S, ? extends B>>>coerce()
                      .resolveService(services))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <B> Service<B> flatMap(Fn1<? super S, ? extends Monad<B, Service<?>>> f) {
        return new Service<>(readerT((Fn1<Services, IO<B>>) services -> fn.<IO<S>>runReaderT(services)
                                                                          .flatMap(s -> $(f, s)
                                                                              .<Service<B>>coerce()
                                                                              .resolveService(services))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <B> Service<B> trampolineM(Fn1<? super S, ? extends MonadRec<RecursiveResult<S, B>, Service<?>>> f) {
        return new Service<>(readerT((Fn1<Services, IO<B>>) services -> fn
            .<IO<S>>runReaderT(services)
            .trampolineM(s -> $(f, s)
                .<Service<RecursiveResult<S, B>>>coerce()
                .resolveService(services))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <B> Lazy<Service<B>> lazyZip(Lazy<? extends Applicative<Fn1<? super S, ? extends B>, Service<?>>> lazyAppFn) {
        return MonadRec.super.lazyZip(lazyAppFn).fmap(Functor::coerce);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <B> Service<B> discardL(Applicative<B, Service<?>> appB) {
        return MonadRec.super.discardL(appB).coerce();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <B> Service<S> discardR(Applicative<B, Service<?>> appB) {
        return MonadRec.super.discardR(appB).coerce();
    }

    /**
     * Constructs a pure <code>Service</code> around an {@link IO} that
     * produces the service object.
     *
     * @param <S>     the service type
     * @param service the service object, within {@link IO}
     * @return a <code>Service</code> that resolves to <code>S</code>
     */
    public static <S> Service<S> service(IO<S> service) {
        return new Service<>(readerT(constantly(service)));
    }

    /**
     * Constructs a <code>Service</code> by references using a handle.
     *
     * @param <S>    the service type
     * @param handle the handle to reference
     * @return A reference <code>Service</code>
     */
    public static <S> Service<S> serviceRef(ServiceHandle<S> handle) {
        return new Service<>(readerT(services -> services.resolve(handle)));
    }

    /**
     * Constructs a <code>Service</code> with one dependency. The dependency
     * is referenced by its {@link ServiceHandle}, and made available in the
     * scope of a function that then produces a <code>Service</code>
     *
     * @param <S>    the service type
     * @param <Dep>  the dependency type
     * @param handle a <code>ServiceHandle</code> for the dependency
     * @param fn     a function that can produce the service given the
     *               dependency
     * @return a <code>Service</code> that resolves to <code>S</code>
     */
    public static <S, Dep> Service<S> service(
        ServiceHandle<Dep> handle,
        Fn1<Dep, Service<S>> fn) {
        return serviceRef(handle).flatMap(fn);
    }

    /**
     * Constructs a <code>Service</code> with two dependencies. The
     * dependencies are referenced by their {@link ServiceHandle}s, and made
     * available in the scope of a function that then produces a
     * <code>Service</code>.
     *
     * @param <S>     the service type
     * @param <Dep1>  the first dependency type
     * @param <Dep2>  the second dependency type
     * @param handle1 a <code>ServiceHandle</code> for the first dependency
     * @param handle2 a <code>ServiceHandle</code> for the second dependency
     * @param fn      a function that can produce the service given the
     *                dependencies
     * @return a <code>Service</code> that resolves to <code>S</code>
     */
    public static <S, Dep1, Dep2> Service<S> service(
        ServiceHandle<Dep1> handle1,
        ServiceHandle<Dep2> handle2,
        Fn2<Dep1, Dep2, Service<S>> fn) {
        return join(LiftA2.<Dep1, Dep2, Service<S>, Service<?>, Service<Service<S>>>liftA2(
            fn,
            serviceRef(handle1),
            serviceRef(handle2)));
    }

    /**
     * Constructs a <code>Service</code> with three dependencies. The
     * dependencies are referenced by their {@link ServiceHandle}s, and made
     * available in the scope of a function that then produces a
     * <code>Service</code>.
     *
     * @param <S>     the service type
     * @param <Dep1>  the first dependency type
     * @param <Dep2>  the second dependency type
     * @param <Dep3>  the third dependency type
     * @param handle1 a <code>ServiceHandle</code> for the first dependency
     * @param handle2 a <code>ServiceHandle</code> for the second dependency
     * @param handle3 a <code>ServiceHandle</code> for the third dependency
     * @param fn      a function that can produce the service given the
     *                dependencies
     * @return a <code>Service</code> that resolves to <code>S</code>
     */
    public static <S, Dep1, Dep2, Dep3> Service<S> service(
        ServiceHandle<Dep1> handle1,
        ServiceHandle<Dep2> handle2,
        ServiceHandle<Dep3> handle3,
        Fn3<Dep1, Dep2, Dep3, Service<S>> fn) {
        return join(LiftA3.<Dep1, Dep2, Dep3, Service<S>, Service<?>, Service<Service<S>>>liftA3(
            fn,
            serviceRef(handle1),
            serviceRef(handle2),
            serviceRef(handle3)));
    }

    /**
     * Constructs a <code>Service</code> with four dependencies. The
     * dependencies are referenced by their {@link ServiceHandle}s, and made
     * available in the scope of a function that then produces a
     * <code>Service</code>.
     *
     * @param <S>     the service type
     * @param <Dep1>  the first dependency type
     * @param <Dep2>  the second dependency type
     * @param <Dep3>  the third dependency type
     * @param <Dep4>  the fourth dependency type
     * @param handle1 a <code>ServiceHandle</code> for the first dependency
     * @param handle2 a <code>ServiceHandle</code> for the second dependency
     * @param handle3 a <code>ServiceHandle</code> for the third dependency
     * @param handle4 a <code>ServiceHandle</code> for the fourth dependency
     * @param fn      a function that can produce the service given the
     *                dependencies
     * @return a <code>Service</code> that resolves to <code>S</code>
     */
    public static <S, Dep1, Dep2, Dep3, Dep4> Service<S> service(
        ServiceHandle<Dep1> handle1,
        ServiceHandle<Dep2> handle2,
        ServiceHandle<Dep3> handle3,
        ServiceHandle<Dep4> handle4,
        Fn4<Dep1, Dep2, Dep3, Dep4, Service<S>> fn) {
        return join(LiftA4.<Dep1, Dep2, Dep3, Dep4, Service<S>, Service<?>, Monad<Service<S>, Service<?>>>liftA4(
            fn,
            serviceRef(handle1),
            serviceRef(handle2),
            serviceRef(handle3),
            serviceRef(handle4)));
    }

    /**
     * Constructs a <code>Service</code> with five dependencies. The
     * dependencies are referenced by their {@link ServiceHandle}s, and made
     * available in the scope of a function that then produces a
     * <code>Service</code>.
     *
     * @param <S>     the service type
     * @param <Dep1>  the first dependency type
     * @param <Dep2>  the second dependency type
     * @param <Dep3>  the third dependency type
     * @param <Dep4>  the fourth dependency type
     * @param <Dep5>  the fifth dependency type
     * @param handle1 a <code>ServiceHandle</code> for the first dependency
     * @param handle2 a <code>ServiceHandle</code> for the second dependency
     * @param handle3 a <code>ServiceHandle</code> for the third dependency
     * @param handle4 a <code>ServiceHandle</code> for the fourth dependency
     * @param handle5 a <code>ServiceHandle</code> for the fifth dependency
     * @param fn      a function that can produce the service given the
     *                dependencies
     * @return a <code>Service</code> that resolves to <code>S</code>
     */
    public static <S, Dep1, Dep2, Dep3, Dep4, Dep5> Service<S> service(
        ServiceHandle<Dep1> handle1,
        ServiceHandle<Dep2> handle2,
        ServiceHandle<Dep3> handle3,
        ServiceHandle<Dep4> handle4,
        ServiceHandle<Dep5> handle5,
        Fn5<Dep1, Dep2, Dep3, Dep4, Dep5, Service<S>> fn) {
        return join(LiftA5.<Dep1, Dep2, Dep3, Dep4, Dep5, Service<S>, Service<?>, Monad<Service<S>, Service<?>>>liftA5(
            fn,
            serviceRef(handle1),
            serviceRef(handle2),
            serviceRef(handle3),
            serviceRef(handle4),
            serviceRef(handle5)));
    }

    /**
     * Constructs a <code>Service</code> with six dependencies. The
     * dependencies are referenced by their {@link ServiceHandle}s, and made
     * available in the scope of a function that then produces a
     * <code>Service</code>.
     *
     * @param <S>     the service type
     * @param <Dep1>  the first dependency type
     * @param <Dep2>  the second dependency type
     * @param <Dep3>  the third dependency type
     * @param <Dep4>  the fourth dependency type
     * @param <Dep5>  the fifth dependency type
     * @param <Dep6>  the sixth dependency type
     * @param handle1 a <code>ServiceHandle</code> for the first dependency
     * @param handle2 a <code>ServiceHandle</code> for the second dependency
     * @param handle3 a <code>ServiceHandle</code> for the third dependency
     * @param handle4 a <code>ServiceHandle</code> for the fourth dependency
     * @param handle5 a <code>ServiceHandle</code> for the fifth dependency
     * @param handle6 a <code>ServiceHandle</code> for the sixth dependency
     * @param fn      a function that can produce the service given the
     *                dependencies
     * @return a <code>Service</code> that resolves to <code>S</code>
     */
    public static <S, Dep1, Dep2, Dep3, Dep4, Dep5, Dep6>
    Service<S> service(
        ServiceHandle<Dep1> handle1,
        ServiceHandle<Dep2> handle2,
        ServiceHandle<Dep3> handle3,
        ServiceHandle<Dep4> handle4,
        ServiceHandle<Dep5> handle5,
        ServiceHandle<Dep6> handle6,
        Fn6<Dep1, Dep2, Dep3, Dep4, Dep5, Dep6, Service<S>> fn) {
        return join(LiftA6.<Dep1, Dep2, Dep3, Dep4, Dep5, Dep6, Service<S>, Service<?>, Monad<Service<S>, Service<?>>>liftA6(
            fn,
            serviceRef(handle1),
            serviceRef(handle2),
            serviceRef(handle3),
            serviceRef(handle4),
            serviceRef(handle5),
            serviceRef(handle6)));
    }

    /**
     * Constructs a <code>Service</code> with seven dependencies. The
     * dependencies are referenced by their {@link ServiceHandle}s, and made
     * available in the scope of a function that then produces a
     * <code>Service</code>.
     *
     * @param <S>     the service type
     * @param <Dep1>  the first dependency type
     * @param <Dep2>  the second dependency type
     * @param <Dep3>  the third dependency type
     * @param <Dep4>  the fourth dependency type
     * @param <Dep5>  the fifth dependency type
     * @param <Dep6>  the sixth dependency type
     * @param <Dep7>  the seventh dependency type
     * @param handle1 a <code>ServiceHandle</code> for the first dependency
     * @param handle2 a <code>ServiceHandle</code> for the second dependency
     * @param handle3 a <code>ServiceHandle</code> for the third dependency
     * @param handle4 a <code>ServiceHandle</code> for the fourth dependency
     * @param handle5 a <code>ServiceHandle</code> for the fifth dependency
     * @param handle6 a <code>ServiceHandle</code> for the sixth dependency
     * @param handle7 a <code>ServiceHandle</code> for the seventh dependency
     * @param fn      a function that can produce the service given the
     *                dependencies
     * @return a <code>Service</code> that resolves to <code>S</code>
     */
    public static <S, Dep1, Dep2, Dep3, Dep4, Dep5, Dep6, Dep7>
    Service<S> service(
        ServiceHandle<Dep1> handle1,
        ServiceHandle<Dep2> handle2,
        ServiceHandle<Dep3> handle3,
        ServiceHandle<Dep4> handle4,
        ServiceHandle<Dep5> handle5,
        ServiceHandle<Dep6> handle6,
        ServiceHandle<Dep7> handle7,
        Fn7<Dep1, Dep2, Dep3, Dep4, Dep5, Dep6, Dep7, Service<S>> fn) {
        return join(LiftA7.<Dep1, Dep2, Dep3, Dep4, Dep5, Dep6, Dep7, Service<S>, Service<?>, Monad<Service<S>, Service<?>>>liftA7(
            fn,
            serviceRef(handle1),
            serviceRef(handle2),
            serviceRef(handle3),
            serviceRef(handle4),
            serviceRef(handle5),
            serviceRef(handle6),
            serviceRef(handle7)));
    }
}
