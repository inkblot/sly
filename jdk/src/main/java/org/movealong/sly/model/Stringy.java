/*
 * Copyright (c) 2023 Nate Riffe
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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Iterator;

import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static lombok.AccessLevel.PRIVATE;

/**
 * For unknown reasons, the Java API does not implement native iteration of the
 * characters that make up a {@link String} by implementing {@link Iterable}.
 * <code>Stringy</code> is a {@link CharSequence} decorator which offers just
 * this missing functionality, as well as easy conversions between
 * {@link CharSequence}, {@link Iterable} of {@link Character}, and
 * {@link String}.
 * <p>
 * Useful conversions:
 * <dl>
 *     <dt>{@link CharSequence} to {@link String}</dt>
 *     <dd>
 *         <pre>
 *             stringify(new StringBuilder("foobar"))
 *         </pre>
 *     </dd>
 *     <dt>{@link Iterable} to {@link String}</dt>
 *     <dd>
 *         <pre>
 *             stringify(strictQueue('f', 'o', 'o', 'b', 'a', 'r'))
 *         </pre>
 *     </dd>
 *     <dt>{@link CharSequence} to {@link Iterable}</dt>
 *     <dd>
 *         <pre>
 *             stringy("foobar")
 *         </pre>
 *     </dd>
 *     <dt>{@link Iterable} to {@link CharSequence}</dt>
 *     <dd>
 *         <pre>
 *             stringy(strictQueue('f', 'o', 'o', 'b', 'a', 'r'))
 *         </pre>
 *     </dd>
 * </dl>
 * <p>
 * Certain operations are idempotent. In general, attempting to construct a
 * type from an object that already expresses that type will return the
 * supplied object. For example, <code>stringify("foobar")</code> will detect
 * that the supplied {@link CharSequence} is already a {@link String} and
 * simply return it. This is also the case when attempting to construct a
 * <code>Stringy</code> from an existing {@link Stringy}.
 */
@EqualsAndHashCode
@AllArgsConstructor(access = PRIVATE)
public final class Stringy implements CharSequence, Iterable<Character> {

    @Delegate private final CharSequence chars;

    @Override
    public Iterator<Character> iterator() {
        return new CharacterIterator(Stringy.this.chars);
    }

    public String stringValue() {
        return stringify((Iterable<Character>) this);
    }

    public String toString() {
        return stringValue();
    }

    public static String stringify(Iterable<Character> chars) {
        return foldLeft(StringBuilder::append, new StringBuilder(), chars).toString();
    }

    public static String stringify(CharSequence chars) {
        if (chars instanceof String) return (String) chars;
        return stringify(() -> new CharacterIterator(chars));
    }

    public static Stringy stringy(CharSequence chars) {
        if (chars instanceof Stringy) return (Stringy) chars;
        return new Stringy(chars);
    }

    public static Stringy stringy(Iterable<Character> chars) {
        if (chars instanceof Stringy) return (Stringy) chars;
        return stringy(stringify(chars));
    }

    @RequiredArgsConstructor(access = PRIVATE)
    private static class CharacterIterator implements Iterator<Character> {
        private final CharSequence chars;
        private       int          index = 0;

        @Override
        public boolean hasNext() {
            return chars.length() > index;
        }

        @Override
        public Character next() {
            return chars.charAt(index++);
        }
    }
}
