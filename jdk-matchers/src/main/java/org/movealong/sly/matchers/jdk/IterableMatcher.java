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
package org.movealong.sly.matchers.jdk;

import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.IsEqual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Cons.cons;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;
import static org.movealong.sly.hamcrest.IndentingDescription.indentWith;

/**
 * Match an {@link Iterable} and the elements it iterates over.
 *
 * @param <E> the type of the elements
 */
@AllArgsConstructor(access = PRIVATE)
public class IterableMatcher<E> extends TypeSafeDiagnosingMatcher<Iterable<E>> {

    private final Iterable<? extends Matcher<? super E>> matchers;
    private final String                                 typeDescriptor;

    @Override
    protected boolean matchesSafely(Iterable<E> values, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendText(typeDescriptor);
        Iterator<? extends Matcher<? super E>> matcherIterator = matchers.iterator();
        Iterator<E>                            valuesIterator  = values.iterator();
        int                                    index           = 0;
        boolean                                elementMatch    = true;

        while (matcherIterator.hasNext() && valuesIterator.hasNext()) {
            Matcher<? super E> matcher = matcherIterator.next();
            E                  value   = valuesIterator.next();

            if (!matcher.matches(value)) {
                if (elementMatch) {
                    mismatchDescription.appendText("\n\t| with mismatches:");
                }
                mismatchDescription
                    .appendText("\n\t\t").appendText(Integer.toString(index)).appendText(": ")
                    .appendDescriptionOf(indentWith("\t\t\t", d -> matcher.describeMismatch(value, d)));
                elementMatch = false;
            }
            index++;
        }

        List<E> surplus = toCollection(ArrayList::new, () -> valuesIterator);
        if (!surplus.isEmpty()) {
            mismatchDescription.appendText("\n\t| with surplus elements:");
            for (E value : surplus) {
                mismatchDescription.appendText("\n\t\t")
                                   .appendText(Integer.toString(index++))
                                   .appendText(": ")
                                   .appendDescriptionOf(indentWith("\t\t\t", d -> d.appendValue(value)));
            }
            return false;
        }

        List<? extends Matcher<? super E>> missing = toCollection(ArrayList::new, () -> matcherIterator);
        if (!missing.isEmpty()) {
            mismatchDescription.appendText("\n\t| with missing elements:");
            for (Matcher<? super E> matcher : missing) {
                mismatchDescription.appendText("\n\t\t")
                                   .appendText(Integer.toString(index++))
                                   .appendText(": ")
                                   .appendDescriptionOf(indentWith("\t\t\t", matcher));
            }
            return false;
        }

        return elementMatch;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(typeDescriptor).appendText(" that:");
        Iterator<? extends Matcher<? super E>> matcherIterator = matchers.iterator();
        int                                    index           = 0;
        while (matcherIterator.hasNext()) {
            description.appendText("\n\t")
                       .appendText(Integer.toString(index++))
                       .appendText(": ")
                       .appendDescriptionOf(indentWith("\t\t", matcherIterator.next()));
        }
    }

    /**
     * Creates a matcher which is satisfied by an {@link Iterable} that
     * iterates elements of type <code>E</code> that are equal to the values
     * supplied in <code>first</code> and <code>rest</code>. Equality is
     * determined according to the {@link Object#equals} method of the
     * evaluated element. There must be no excess or missing elements in
     * the evaluated {@link Iterable} and the elements must occur in the
     * same order as the values that they are equal to.
     *
     * @param first the value that the first iterated element must be equal to
     * @param rest  the values that the remaining iterated elements must be
     *              equal to
     * @param <E>   the element type
     * @return A matcher of {@link Iterable}
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> IterableMatcher<E> iterates(E first, E... rest) {
        return iteratesAll(cons(first, asList(rest)));
    }

    /**
     * Creates a matcher which is satisfied by an {@link Iterable} that
     * iterates elements of type <code>E</code> that are equal to the values
     * supplied in <code>elements</code>. Equality is determined according to
     * the {@link Object#equals} method of the evaluated element. There must be
     * no excess or missing elements in the evaluated {@link Iterable} and they
     * must occur in the same order as the values they are equal to.
     *
     * @param elements the values that the iterated elements must be equal to
     * @param <E>      the element type
     * @return A matcher of {@link Iterable}
     */
    public static <E> IterableMatcher<E> iteratesAll(Iterable<E> elements) {
        return iteratesAllItemsThat(map(IsEqual::equalTo, elements));
    }

    /**
     * Creates a matcher which is satisfied by an {@link Iterable} that
     * iterates elements of type <code>E</code> that satisfy the element
     * matchers supplied in <code>first</code> and <code>rest</code>. There
     * must be no excess or missing elements in the evaluated {@link Iterable}
     * and they must occur in the same order as the element matchers that they
     * satisfy.
     *
     * @param first the matcher that the first iterated element must satisfy
     * @param rest  the matchers that the remaining iterated elements must
     *              satisfy
     * @param <E>   the element type
     * @return A matcher of {@link Iterable}
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> IterableMatcher<E> iteratesItemsThat(Matcher<? super E> first, Matcher<? super E>... rest) {
        return iteratesAllItemsThat(cons(first, asList(rest)));
    }

    /**
     * Creates a matcher which is satisfied by an {@link Iterable} that
     * iterates elements of type <code>E</code> that satisfy the element
     * matchers supplied in <code>matchers</code>. There must be no excess or
     * missing elements in the evaluated {@link Iterable} and they must occur
     * in the same order as the element matchers that they satisfy.
     *
     * @param <E>      the element type
     * @param matchers the matchers that the iterated elements must satisfy
     * @return A matcher of {@link Iterable}
     */
    public static <E> IterableMatcher<E> iteratesAllItemsThat(Iterable<? extends Matcher<? super E>> matchers) {
        return derivativeOfIterableMatcher(matchers, "an iterable");
    }

    /**
     * Creates a matcher which is satisfied by an {@link Iterable} that
     * iterates no elements.
     *
     * @param <E> the element type
     * @return A matcher of {@link Iterable}
     */
    public static <E> IterableMatcher<E> isEmptyIterable() {
        return iteratesAllItemsThat(strictQueue());
    }

    /**
     * A static constructor for use in derived matchers, where the evaluated
     * type is not {@link Iterable} and a different descriptor for the type
     * might be more appropriate.
     *
     * @param <E>            the element type
     * @param matchers       the matchers that the iterated elements must satisfy
     * @param typeDescriptor the descriptor of the evaluated type
     * @return A matcher of {@link Iterable}
     */
    public static <E> IterableMatcher<E>
    derivativeOfIterableMatcher(Iterable<? extends Matcher<? super E>> matchers, String typeDescriptor) {
        return new IterableMatcher<>(matchers, typeDescriptor);
    }
}
