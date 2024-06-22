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
package org.movealong.sly.jdk;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.Monad;
import lombok.AllArgsConstructor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.io.IO.io;
import static lombok.AccessLevel.PRIVATE;

/**
 * An atom is a reference value that can be updated atomically.
 *
 * @param <A> The type of object referenced by the atom
 */
@AllArgsConstructor(access = PRIVATE)
public final class Atom<A> {
    private static final VarHandle HANDLE;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            HANDLE = lookup.findVarHandle(Atom.class, "a", Object.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private boolean weakCompareAndSet(A prev, A next) {
        return HANDLE.weakCompareAndSet(this, prev, next);
    }

    private volatile A a;

    /**
     * Returns an {@link IO} that will atomically update the current value with
     * the results of applying the given function, returning a tuple of the
     * original value and the updated value. When there is contention between
     * the update and another thread, the update will be retried. This can
     * happen repeatedly until the update succeeds.
     *
     * @param f a function that effectfully produces the next value
     * @return an {@link IO} that will atomically update the current value
     */
    public IO<Tuple2<A, A>> update(Fn1<A, ? extends Monad<A, IO<?>>> f) {
        return io(() -> {
            A prev, next;
            do {
                prev = a;
                next = f.apply(prev).<IO<A>>coerce().unsafePerformIO();
            } while (!weakCompareAndSet(prev, next));
            return tuple(prev, next);
        });
    }

    /**
     * Returns an {@link IO} that will atomically update the current value with
     * the results of applying the given function, returning the original
     * value. When there is contention between the update and another thread,
     * the update will be retried. This can happen repeatedly until the update
     * succeeds.
     *
     * @param f a function that effectfully produces the next value
     * @return an {@link IO} that will atomically update the current value
     */
    public IO<A> getAndUpdate(Fn1<A, ? extends Monad<A, IO<?>>> f) {
        return update(f).fmap(Tuple2::_1);
    }

    /**
     * Returns an {@link IO} that will atomically update the current value with
     * the results of applying the given function, returning the updated value.
     * When there is contention between the update and another thread, the
     * update will be retried. This can happen repeatedly until the update
     * succeeds.
     *
     * @param f a function that effectfully produces the next value
     * @return an {@link IO} that will atomically update the current value
     */
    public IO<A> updateAndGet(Fn1<A, ? extends Monad<A, IO<?>>> f) {
        return update(f).fmap(Tuple2::_2);
    }

    /**
     * Returns an {@link IO} atomically compares the current value with the
     * given previous value, and if they are equal, sets the value to the given
     * next value.
     *
     * @param prev the value to compare against
     * @param next the value to set
     * @return an {@link IO} that will atomically compare and set the current
     * value
     */
    public IO<Boolean> compareAndSet(A prev, A next) {
        return io(() -> HANDLE.compareAndSet(this, prev, next));
    }

    /**
     * Returns an {@link IO} that will return the current value of the atom
     * when invoked.
     *
     * @return an {@link IO} that will return the current value
     */
    public IO<A> get() {
        return io(() -> a);
    }

    /**
     * Return an {@link IO} that will set the value of the atom to the given
     * value.
     *
     * @param a the new value
     * @return an {@link IO} that will set the value
     */
    public IO<Unit> set(A a) {
        return io(() -> {this.a = a;});
    }

    /**
     * Construct an {@link Atom} with the given initial value.
     *
     * @param <A> the type that the atom holds
     * @param a   the initial value
     * @return an {@link Atom} initialized with the given value
     */
    public static <A> Atom<A> atom(A a) {
        return new Atom<>(a);
    }
}
