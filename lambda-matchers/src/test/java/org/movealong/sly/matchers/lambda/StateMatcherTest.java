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

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functor.builtin.State;
import com.jnape.palatable.shoki.impl.StrictQueue;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iterates;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static org.movealong.sly.matchers.lambda.StateMatcher.stateThat;

class StateMatcherTest {

    @Test
    void fullMatching() {
        StateMatcher<StrictQueue<String>, Maybe<String>> subject =
            StateMatcher.<StrictQueue<String>, Maybe<String>>stateThat(
                            iterates("b", "c", "d"),
                            isJustOf("a"))
                        .afterEvaluating(strictQueue("a", "b", "c", "d"));

        assertTrue(subject.matches(pop()));
        assertFalse(subject.matches(push("e")));
    }

    @Test
    void partialMatching() {
        StateMatcher<StrictQueue<String>, Maybe<String>> subject =
            StateMatcher.<StrictQueue<String>, Maybe<String>>stateValueThat(isJustOf("a"))
                        .afterEvaluating(strictQueue("a", "b", "c", "d"));

        assertTrue(subject.matches(pop()));
        assertFalse(subject.matches(pop().discardL(pop())));
        assertFalse(subject.matches(push("e")));
    }

    @Test
    void describesCorrectly() {
        Matcher<Integer> stateMatcher = lessThan(5);
        Matcher<Integer> valueMatcher = greaterThan(6);
        StateMatcher<Integer, Integer> subject =
            stateThat(stateMatcher,
                      valueMatcher)
                .afterEvaluating(242);

        assertEquals(
            "State that evaluates <242> to produce\n" +
                "\t| final state: " + descriptionOf(stateMatcher) + "\n" +
                "\t| value: " + descriptionOf(valueMatcher),
            descriptionOf(subject));
    }

    static State<StrictQueue<String>, Maybe<String>> pop() {
        return state(q -> tuple(q.head(), q.tail()));
    }

    static State<StrictQueue<String>, Maybe<String>> push(String value) {
        return state(q -> tuple(nothing(), q.snoc(value)));
    }
}