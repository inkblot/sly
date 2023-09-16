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

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functor.builtin.State;
import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.hamcrest.IndentingDescription.indentWith;

/**
 * Given an initial state, does a {@link State} evaluate to a final state and a
 * carrier that each satisfy their respective matchers?
 *
 * @param <S> the state type
 * @param <A> the carrier type
 */
@AllArgsConstructor(access = PRIVATE)
public class StateMatcher<S, A> extends TypeSafeDiagnosingMatcher<State<S, A>> {

    private final S                  initialState;
    private final Matcher<? super S> stateMatcher;
    private final Matcher<? super A> valueMatcher;

    @Override
    protected boolean matchesSafely(State<S, A> item, Description mismatchDescription) {
        Tuple2<A, S> result       = item.run(initialState);
        boolean      stateMatches = stateMatcher.matches(result._2());
        boolean      valueMatches = valueMatcher.matches(result._1());
        boolean      matches      = stateMatches && valueMatches;

        if (!matches) {
            mismatchDescription
                .appendText("State that evaluated ")
                .appendValue(initialState)
                .appendText(" and produced:");
        }

        if (!stateMatches) {
            mismatchDescription
                .appendText("\n\t| final state: ")
                .appendDescriptionOf(indentWith("\t\t", stateMatcher))
                .appendText("\n\t  but ")
                .appendDescriptionOf(indentWith("\t\t", d -> stateMatcher.describeMismatch(result._2(), d)));
        }

        if (!valueMatches) {
            mismatchDescription
                .appendText("\n\t| value: ")
                .appendDescriptionOf(indentWith("\t\t", valueMatcher))
                .appendText("\n\t  but ")
                .appendDescriptionOf(indentWith("\t\t", d -> valueMatcher.describeMismatch(result._1(), d)));
        }

        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description
            .appendText("State that evaluates ")
            .appendValue(initialState)
            .appendText(" to produce\n\t| final state: ")
            .appendDescriptionOf(stateMatcher)
            .appendText("\n\t| value: ")
            .appendDescriptionOf(valueMatcher);
    }

    /**
     * Produce a {@link Builder} of a {@link StateMatcher} which will be
     * satisfied by a final state that satisfies the supplied matcher.
     *
     * @param <S>          the state type
     * @param stateMatcher the matcher of the final state
     * @return A builder that will produce a StateMatcher with an
     * opinion about the final state
     */
    public static <S> Builder<S, ?> finalStateThat(Matcher<? super S> stateMatcher) {
        return new Builder<>(stateMatcher, anything());
    }

    /**
     * Produce a {@link Builder} of a {@link StateMatcher} which will be
     * satisfied by a final state that is equal to the supplied value
     * according to the {@link Object#equals} method of the evaluated
     * object.
     *
     * @param <S>        the state type
     * @param finalState the final state
     * @return A builder that will produce a StateMatcher with an
     * opinion about the final state
     */
    public static <S> Builder<S, ?> finalStateOf(S finalState) {
        return new Builder<>(equalTo(finalState), anything());
    }

    /**
     * Produce a {@link Builder} of a {@link StateMatcher} which will be
     * satisfied by a carrier value that satisfies the supplied matcher.
     *
     * @param <S>          the state type
     * @param <A>          the carrier type
     * @param valueMatcher the matcher of the carrier type
     * @return A builder that will produce a StateMatcher with an
     * opinion about the carrier value
     */
    public static <S, A> Builder<S, A> stateValueThat(Matcher<? super A> valueMatcher) {
        return new Builder<>(anything(), valueMatcher);
    }

    /**
     * Produce a {@link Builder} of a {@link StateMatcher} which will be
     * satisfied by a carrier value that is equal to the supplied value
     * according to the {@link Object#equals} method of the evaluated
     * object.
     *
     * @param <S>   the state type
     * @param <A>   the carrier type
     * @param value the carrier value
     * @return A builder that will produce a StateMatcher with an
     * opinion about the carrier value
     */
    public static <S, A> Builder<S, A> stateValueOf(A value) {
        return new Builder<>(anything(), equalTo(value));
    }

    /**
     * Produce a {@link Builder} of a {@link StateMatcher} which will be
     * satisfied if both the final state satisfies the supplied
     * <code>stateMatcher</code> and the carrier value satisfies the
     * supplied <code>stateMatcher</code>
     *
     * @param <S>          the state type
     * @param <A>          the carrier type
     * @param stateMatcher the matcher of the final state
     * @param valueMatcher the matcher of the carrier type
     * @return A builder that will produce a StateMatcher with an
     * opinion about the carrier value
     */
    public static <S, A> Builder<S, A> stateThat(Matcher<? super S> stateMatcher,
                                                 Matcher<? super A> valueMatcher) {
        return new Builder<>(stateMatcher, valueMatcher);
    }

    /**
     * Encapsulates opinions about the final state and carrier values that
     * result from running {@link State}, and allows the construction of a
     * {@link StateMatcher}.
     *
     * @param <S> the state type
     * @param <A> the carrier type
     */
    @AllArgsConstructor(access = PRIVATE)
    public static final class Builder<S, A> {
        private final Matcher<? super S> stateMatcher;
        private final Matcher<? super A> valueMatcher;

        /**
         * Combines the supplied initial state with the encapsulated opinions
         * about the final state and carrier value to produce a
         * {@link StateMatcher}.
         *
         * @param initialState
         * @return a matcher of {@link State}
         */
        public StateMatcher<S, A> afterEvaluating(S initialState) {
            return new StateMatcher<>(initialState, stateMatcher, valueMatcher);
        }
    }
}