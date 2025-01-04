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

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.io.IO;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.movealong.sly.model.Failures;
import org.movealong.sly.model.Label;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.io.IO.io;
import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;

@With
@RequiredArgsConstructor(access = PRIVATE)
public class FailuresAscribedMatcher extends TypeSafeDiagnosingMatcher<Failures> {

    private final Matcher<? super Label>    ascriptionThat;
    private final Matcher<? super Failures> ascribedThat;

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
                   multiple -> io(() -> mismatchDescription
                       .appendText("a multiple failure that ")
                       .appendValue(multiple))
                       .fmap(constantly(false)),
                   matchesAscribed(mismatchDescription))
            .unsafePerformIO();
    }

    private IO<Boolean> matchesAscribed(Failures.Ascribed subject, Description mismatchDescription) {
        return io(() -> {
            boolean matches = true;
            mismatchDescription.appendText("an ascribed failure");
            if (!ascriptionThat.matches(subject.getAscription())) {
                mismatchDescription
                    .appendText(" with ascription that ")
                    .appendDescriptionOf(ascriptionThat)
                    .appendText(" but ")
                    .appendDescriptionOf(d -> ascriptionThat.describeMismatch(subject.getAscription(), d));
                matches = false;
            }

            if (!ascribedThat.matches(subject.getAscribed())) {
                mismatchDescription
                    .appendText(matches ? " with" : " and")
                    .appendText(" ascribed that ")
                    .appendDescriptionOf(ascribedThat)
                    .appendText(" but ")
                    .appendDescriptionOf(d -> ascribedThat.describeMismatch(subject.getAscribed(), d));
                matches = false;
            }
            return matches;
        });
    }

    private Fn1<Failures.Ascribed, IO<Boolean>> matchesAscribed(Description mismatchDescription) {
        return ascribed -> matchesAscribed(ascribed, mismatchDescription);
    }

    @Override
    public void describeTo(Description description) {
        description
            .appendText("a ascribed failure with ascription that ")
            .appendDescriptionOf(ascriptionThat)
            .appendText(" and ascribed that ")
            .appendDescriptionOf(ascribedThat);
    }

    public FailuresAscribedMatcher withAscriptionOf(Label ascription) {
        return withAscriptionThat(equalTo(ascription));
    }

    public FailuresAscribedMatcher withAscribedOf(Failures ascribed) {
        return withAscribedThat(equalTo(ascribed));
    }

    public static FailuresAscribedMatcher isAscribed() {
        return new FailuresAscribedMatcher(anything(), anything());
    }
}
