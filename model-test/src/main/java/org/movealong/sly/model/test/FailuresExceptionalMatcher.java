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
import static org.hamcrest.core.IsSame.sameInstance;

@RequiredArgsConstructor(access = PRIVATE)
public class FailuresExceptionalMatcher extends TypeSafeDiagnosingMatcher<Failures> {

    private final Matcher<? super Throwable> exceptionMatcher;

    @Override
    protected boolean matchesSafely(Failures subject, Description mismatchDescription) {
        return subject.match(
                          message -> io(() -> mismatchDescription
                              .appendText("a message failure that ")
                              .appendValue(message))
                              .fmap(constantly(false)),
                          e -> ifThenElse(exceptionMatcher::matches,
                                          __ -> io(true),
                                          exceptional -> io(() -> mismatchDescription
                                              .appendText("an exceptional failure that ")
                                              .appendDescriptionOf(d -> exceptionMatcher.describeMismatch(exceptional, d)))
                                              .fmap(constantly(false)),
                                          e.getException()),
                          multiple -> io(() -> mismatchDescription
                              .appendText("a multiple failure that ")
                              .appendValue(subject))
                              .fmap(constantly(false)),
                          ascribed -> io(() -> mismatchDescription
                              .appendText("an ascribed failure that ")
                              .appendValue(subject))
                              .fmap(constantly(false)))
                      .unsafePerformIO();
    }

    @Override
    public void describeTo(Description description) {
        description
            .appendText("an exceptional failure that ")
            .appendDescriptionOf(exceptionMatcher);
    }

    public static FailuresExceptionalMatcher isExceptionalThat(Matcher<? super Throwable> exceptionMatcher) {
        return new FailuresExceptionalMatcher(exceptionMatcher);
    }

    public static FailuresExceptionalMatcher isExceptionalOf(Throwable exception) {
        return new FailuresExceptionalMatcher(sameInstance(exception));
    }

    public static FailuresExceptionalMatcher isExceptional() {
        return isExceptionalThat(anything());
    }
}
