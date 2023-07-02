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
package org.movealong.sly.matchers.lambda;

import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.jnape.palatable.winterbourne.StreamT;
import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;
import org.movealong.sly.matchers.jdk.IterableMatcher;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Cons.cons;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;

/**
 * Match a {@link StreamT} and the awaited elements it streams.
 *
 * @param <E> the type of the elements
 */
@AllArgsConstructor(access = PRIVATE)
public class StreamTMatcher<E> extends TypeSafeMatcher<StreamT<Identity<?>, E>> {

    private final IterableMatcher<E> matcher;

    @Override
    protected boolean matchesSafely(StreamT<Identity<?>, E> item) {
        return matcher.matches(item.foldAwait((StrictQueue<E> q, E e) -> new Identity<>(q.snoc(e)),
                                              new Identity<>(strictQueue()))
                                   .runIdentity());
    }

    @Override
    public void describeTo(Description description) {
        matcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(StreamT<Identity<?>, E> item, Description mismatchDescription) {
        matcher.describeMismatch(item.foldAwait((StrictQueue<E> q, E e) -> new Identity<>(q.snoc(e)),
                                                new Identity<>(strictQueue()))
                                     .runIdentity(),
                                 mismatchDescription);
    }

    /**
     * Creates a matcher which is satisfied by a {@link StreamT} that streams
     * elements of type <code>E</code> that are equal to the values supplied in
     * <code>first</code> and <code>rest</code>. Equality is determined
     * according to the {@link Object#equals} method of the evaluated element.
     * There must be no excess or missing elements in the evaluated
     * {@link StreamT} and the elements must occur in the same order as the
     * values they are equal to.
     *
     * @param first the value that the first streamed element must be equal to
     * @param rest  the values that the remaining streamed elements must be
     *              equal to
     * @param <E>   the element type
     * @return A matcher of {@link StreamT}
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> StreamTMatcher<E> streams(E first, E... rest) {
        return streamsAll(cons(first, asList(rest)));
    }

    /**
     * Creates a matcher which is satisfied by a {@link StreamT} that streams
     * elements of type <code>E</code> that are equal to the values supplied in
     * <code>elements</code>. Equality is determined according to the
     * {@link Object#equals} method of the evaluated element. There must be no
     * excess or missing elements in the evaluated {@link StreamT} and the
     * elements must occur in the same order as the values they are equal to.
     *
     * @param elements the values that the streamed elements must be equal to
     * @param <E>      the element type
     * @return A matcher of {@link StreamT}
     */
    public static <E> StreamTMatcher<E> streamsAll(Iterable<E> elements) {
        return streamsAllItemsThat(map(IsEqual::equalTo, elements));
    }

    /**
     * Creates a matcher which is satisfied by a {@link StreamT} that streams
     * elements of type <code>E</code> that satisfy the element matchers
     * supplied in <code>first</code> and <code>rest</code>. There must be no
     * excess or missing elements in the evaluated {@link StreamT} and the
     * elements must occur in the same order as the element matcher that they
     * satisfy.
     *
     * @param first the matcher that the first streamed element must satisfy
     * @param rest  the matchers that the remaining streamed elements must
     *              satisfy
     * @param <E>   the element type
     * @return A matcher of {@link StreamT}
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> StreamTMatcher<E> streamsItemsThat(Matcher<? super E> first, Matcher<? super E>... rest) {
        return streamsAllItemsThat(cons(first, asList(rest)));
    }

    /**
     * Creates a matcher which is satisfied by a {@link StreamT} that streams
     * elements of type <code>E</code> that satisfy the element matchers
     * supplied in <code>matchers</code>. There must be no excess or missing
     * elements in the evaluated {@link StreamT} and the elements must occur in
     * the same order as the element matchers that they satisfy.
     *
     * @param matchers the matcher that the first streamed element must satisfy
     * @param <E>      the element type
     * @return A matcher of {@link StreamT}
     */
    public static <E> StreamTMatcher<E> streamsAllItemsThat(Iterable<? extends Matcher<? super E>> matchers) {
        return new StreamTMatcher<>(IterableMatcher.derivativeOfIterableMatcher(matchers, "a stream"));
    }

    /**
     * Creates a matcher that is satisfied by a {@link StreamT} that streams
     * no elements.
     *
     * @param <E> the element type
     * @return A matcher of {@link StreamT}
     */
    public static <E> StreamTMatcher<E> isEmptyStreamT() {
        return streamsAllItemsThat(strictQueue());
    }
}
