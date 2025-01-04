/*
 * Copyright (c) 2025 Nate Riffe
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
package org.movealong.sly.model.test;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.movealong.sly.model.Failures;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn4.IfThenElse.ifThenElse;
import static com.jnape.palatable.lambda.io.IO.io;
import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.core.IsAnything.anything;

@RequiredArgsConstructor(access = PRIVATE)
public class FailuresMultipleMatcher extends TypeSafeDiagnosingMatcher<Failures> {

    private final Matcher<? super Iterable<Failures>> failuresMatcher;

    @Override
    protected boolean matchesSafely(Failures subject, Description mismatchDescription) {
        return subject
            .match(message -> io(() -> mismatchDescription
                       .appendText("a message failure that ")
                       .appendValue(message))
                       .fmap(constantly(false)),
                   exceptional -> io(() -> mismatchDescription
                       .appendText("an exceptional failure that ")
                       .appendValue(exceptional))
                       .fmap(constantly(false)),
                   m -> ifThenElse(failuresMatcher::matches,
                                   __ -> io(true),
                                   exceptional -> io(() -> mismatchDescription
                                       .appendText("a multiple failure that ")
                                       .appendDescriptionOf(d -> failuresMatcher.describeMismatch(exceptional, d)))
                                       .fmap(constantly(false)),
                                   m.getFailures()),
                   ascribed -> io(() -> mismatchDescription
                       .appendText("an ascribed failure that ")
                       .appendValue(ascribed))
                       .fmap(constantly(false)))
            .unsafePerformIO();
    }

    @Override
    public void describeTo(Description description) {
        description
            .appendText("a multiple failure that ")
            .appendDescriptionOf(failuresMatcher);
    }

    public static FailuresMultipleMatcher isMultipleThat(Matcher<? super Iterable<Failures>> failuresMatcher) {
        return new FailuresMultipleMatcher(failuresMatcher);
    }

    public static FailuresMultipleMatcher isMultiple() {
        return new FailuresMultipleMatcher(anything());
    }
}
