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

import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static com.jnape.palatable.lambda.adt.choice.Choice2.a;
import static com.jnape.palatable.lambda.adt.choice.Choice2.b;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsSame.sameInstance;

/**
 * Match the success or failure of a {@link Try} and then the success value or
 * <code>Throwable</code> that it encapsulates.
 *
 * @param <A> the carrier type
 */
@AllArgsConstructor(access = PRIVATE)
public final class TryMatcher<A> extends TypeSafeMatcher<Try<A>> {

    private final Choice2<Matcher<? super Throwable>, Matcher<? super A>> matcher;

    @Override
    protected void describeMismatchSafely(Try<A> item, Description mismatchDescription) {
        mismatchDescription
            .appendText("was ")
            .appendDescriptionOf(item.match(
                t -> matcher.match(
                    tMatcher -> desc -> tMatcher.describeMismatch(t, desc.appendText("failure that ")),
                    aMatcher -> desc -> desc.appendValue(item)),
                a -> matcher.match(
                    tMatcher -> desc -> desc.appendValue(item),
                    aMatcher -> desc -> aMatcher.describeMismatch(a, desc.appendText("success that ")))));
    }

    @Override
    protected boolean matchesSafely(Try<A> actual) {
        return actual.match(
            t -> matcher.match(
                tMatcher -> tMatcher.matches(t),
                constantly(false)),
            a -> matcher.match(
                constantly(false),
                aMatcher -> aMatcher.matches(a)));
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(matcher.match(
            t -> desc -> desc.appendText("is failure that ")
                             .appendDescriptionOf(t),
            a -> desc -> description.appendText("is success that ")
                                    .appendDescriptionOf(a)));
    }

    /**
     * Creates a matcher that is satisfied by a {@link Try} which is in the
     * failed state with an encapsulated <code>Throwable</code> that satisfies
     * the supplied matcher.
     *
     * @param <A>              the carrier type
     * @param throwableMatcher matcher for the <code>Throwable</code>
     * @return a matcher of {@link Try}
     */
    public static <A> TryMatcher<A> failedTryThat(Matcher<? super Throwable> throwableMatcher) {
        return new TryMatcher<>(a(throwableMatcher));
    }

    /**
     * Creates a matcher that is satisfied by a {@link Try} which is in the
     * failed state, so long as the encapsulated <code>Throwable</code> is
     * a reference to the same <code>Throwable</code> that has been supplied
     *
     * @param <A>      the carrier type
     * @param expected the expected <code>Throwable</code>
     * @return a matcher of {@link Try}
     */
    public static <A> TryMatcher<A> failedTryOf(Throwable expected) {
        return failedTryThat(sameInstance(expected));
    }

    /**
     * Creates a matcher that is satisfied by a {@link Try} which is in the
     * failed state, without any opinion about the encapsulated
     * <code>Throwable</code>.
     *
     * @param <A> the carrier type
     * @return a matcher of {@link Try}
     */
    public static <A> TryMatcher<A> failedTry() {
        return failedTryThat(anything());
    }

    /**
     * Creates a matcher that is satisfied by a {@link Try} which is in the
     * success state with an encapsulated value that satisfies the supplied
     * matcher.
     *
     * @param <A>      the carrier type
     * @param aMatcher matcher of <code>A</code>
     * @return a matcher of {@link Try}
     */
    public static <A> TryMatcher<A> successfulTryThat(Matcher<? super A> aMatcher) {
        return new TryMatcher<>(b(aMatcher));
    }

    /**
     * Creates a matcher that is satisfied by a {@link Try} which is in the
     * success state, so long as the encapsulated value is equal to the
     * supplied value according to the {@link Object#equals} method of the
     * inspected object.
     *
     * @param <A> the carrier type
     * @param a   the expected <code>A</code>
     * @return a matcher of {@link Try}
     */
    public static <A> TryMatcher<A> successfulTryOf(A a) {
        return successfulTryThat(equalTo(a));
    }

    /**
     * Creates a matcher that is satisfied by a {@link Try} which is in the
     * success state, without any opinion about the encapsulated value..
     *
     * @param <A> the carrier type
     * @return a matcher of {@link Try}
     */
    public static <A> TryMatcher<A> successfulTry() {
        return successfulTryThat(anything());
    }
}