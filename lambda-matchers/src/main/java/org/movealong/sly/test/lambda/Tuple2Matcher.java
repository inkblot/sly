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
package org.movealong.sly.test.lambda;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static lombok.AccessLevel.PRIVATE;

/**
 * A {@link Matcher} that matches instances of {@link Tuple2}, given matchers
 * for the elements contained in the tuple.
 *
 * @param <_1> the type of the first element of the tuple
 * @param <_2> the type of the second element of the tuple
 */
@AllArgsConstructor(access = PRIVATE)
public class Tuple2Matcher<_1, _2> extends TypeSafeDiagnosingMatcher<Tuple2<_1, _2>> {

    private final Matcher<? super _1> _1Matcher;
    private final Matcher<? super _2> _2Matcher;

    @Override
    protected boolean matchesSafely(Tuple2<_1, _2> item, Description mismatch) {
        mismatch.appendText("tuple mismatch:");
        boolean elementMatch = true;

        if (!_1Matcher.matches(item._1())) {
            _1Matcher.describeMismatch(
                item._1(),
                mismatch.appendText(" _1: expected ")
                        .appendDescriptionOf(_1Matcher)
                        .appendText(" but "));
            elementMatch = false;
        }

        if (!_2Matcher.matches(item._2())) {
            if (!elementMatch) {
                mismatch.appendText(" and");
            }
            _2Matcher.describeMismatch(
                item._2(),
                mismatch.appendText(" _2: expected ")
                        .appendDescriptionOf(_2Matcher)
                        .appendText(" but "));
            elementMatch = false;
        }

        return elementMatch;
    }

    @Override
    public void describeTo(Description description) {
        description
            .appendText("Tuple of ")
            .appendDescriptionOf(_1Matcher)
            .appendText(" and ")
            .appendDescriptionOf(_2Matcher);
    }

    /**
     * Creates a matcher that matches an instance of {@link Tuple2} when the
     * elements of the tuple satisfy the supplied matchers.
     *
     * @param <_1>      the type of the first element of the tuple
     * @param <_2>      the type of the second element of the tuple
     * @param _1Matcher a matcher for the first element of the tuple
     * @param _2Matcher a matcher for the second element of the tuple
     * @return A matcher of {@link Tuple2}
     */
    public static <_1, _2> Tuple2Matcher<_1, _2> isTuple2That(Matcher<? super _1> _1Matcher,
                                                              Matcher<? super _2> _2Matcher) {
        return new Tuple2Matcher<>(_1Matcher, _2Matcher);
    }
}
