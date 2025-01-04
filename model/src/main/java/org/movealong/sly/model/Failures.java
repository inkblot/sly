/*
 * Copyright (c) 2024-2025 Nate Riffe
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
package org.movealong.sly.model;

import com.jnape.palatable.lambda.adt.coproduct.CoProduct4;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.shoki.impl.StrictQueue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import static com.jnape.palatable.lambda.functions.Fn0.fn0;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Intersperse.intersperse;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.monoid.builtin.Join.join;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static org.movealong.sly.model.Failures.*;

/**
 * {@link Failures} is a general purpose type for representing failure modes.
 * It is composable in two ways, both through the addition of new failures to
 * existing ones to create an aggregate, and by ascribing a failure to a cause
 * identified by a label. Basic singular failures are either messages in the
 * form of a {@link String} or exceptions.
 */
@NoArgsConstructor(access = PRIVATE)
public abstract class Failures implements CoProduct4<Message, Exceptional, Multiple, Ascribed, Failures> {

    /**
     * Returns a new instance of {@link Failures} representing the original
     * instance coupled with an ascription, typically from a {@link Label}
     *
     * @param ascription the cause or label to ascribe the failure to
     * @return The ascribed failure
     */
    public Failures ascribe(Label ascription) {
        return new Ascribed(ascription, this);
    }

    /**
     * Aggregates the {@link Failures} instance on which <code>add</code> is
     * invoked with another {@link Failures}.
     *
     * @param another the additional failure to aggregate
     * @return An aggregate of multiple failures
     */
    public Failures add(Failures another) {
        return new Multiple(another.projectC().match(
            fn0(() -> projectC().match(
                fn0(() -> strictQueue(this, another)),
                these -> these.getFailures().snoc(another))),
            more -> projectC().match(
                fn0(() -> more.getFailures().cons(this)),
                these -> these.getFailures().consAll(more.getFailures()))));
    }

    /**
     * Constructs a simple instance of {@link Failures} representing a message.
     *
     * @param message The text of the message failure
     * @return The message failure
     */
    public static Failures message(String message) {
        return new Message(message);
    }

    /**
     * Constructs a simple instance of {@link Failures} representing a message
     * which is formatted from a format string and a variable enumeration of
     * objects. See {@link String#format} for more details about the formatting
     * process.
     *
     * @param format The format string text of the message failure
     * @param args   Objects referenced in the format string
     * @return The message failure
     */
    public static Failures message(String format, Object... args) {
        return message(format(format, args));
    }

    /**
     * Constructs a simple instance of {@link Failures} representing an
     * exception.
     *
     * @param t The exception
     * @return The exceptional failure
     */
    public static Failures exceptional(Throwable t) {
        return new Exceptional(t);
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor(access = PRIVATE)
    public static class Message extends Failures {
        String message;

        @Override
        public <R> R match(Fn1<? super Message, ? extends R> messageFn,
                           Fn1<? super Exceptional, ? extends R> exceptionalFn,
                           Fn1<? super Multiple, ? extends R> multipleFn,
                           Fn1<? super Ascribed, ? extends R> ascribedFn) {
            return messageFn.apply(this);
        }

        @Override
        public String toString() {
            return format("Failures(message=%s)", message);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor(access = PRIVATE)
    public static class Exceptional extends Failures {
        Throwable exception;

        /**
         * {@inheritDoc}
         */
        @Override
        public <R> R match(Fn1<? super Message, ? extends R> messageFn,
                           Fn1<? super Exceptional, ? extends R> exceptionalFn,
                           Fn1<? super Multiple, ? extends R> multipleFn,
                           Fn1<? super Ascribed, ? extends R> ascribedFn) {
            return exceptionalFn.apply(this);
        }

        @Override
        public String toString() {
            return format("Failures(exception=%s)", exception);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor(access = PRIVATE)
    public static class Multiple extends Failures {
        StrictQueue<Failures> failures;

        /**
         * {@inheritDoc}
         */
        @Override
        public <R> R match(Fn1<? super Message, ? extends R> messageFn,
                           Fn1<? super Exceptional, ? extends R> exceptionalFn,
                           Fn1<? super Multiple, ? extends R> multipleFn,
                           Fn1<? super Ascribed, ? extends R> ascribedFn) {
            return multipleFn.apply(this);
        }

        @Override
        public String toString() {
            return format("Failures(multiple=[%s])", join()
                .foldLeft("", intersperse(",", map(Object::toString, failures))));
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor(access = PRIVATE)
    public static class Ascribed extends Failures {
        Label    ascription;
        Failures ascribed;

        /**
         * {@inheritDoc}
         */
        @Override
        public <R> R match(Fn1<? super Message, ? extends R> messageFn,
                           Fn1<? super Exceptional, ? extends R> exceptionalFn,
                           Fn1<? super Multiple, ? extends R> multipleFn,
                           Fn1<? super Ascribed, ? extends R> ascribedFn) {
            return ascribedFn.apply(this);
        }

        @Override
        public String toString() {
            return format("Failures(ascription=%s,ascribed=%s)",
                          ascription.getValue(),
                          ascribed);
        }
    }
}
