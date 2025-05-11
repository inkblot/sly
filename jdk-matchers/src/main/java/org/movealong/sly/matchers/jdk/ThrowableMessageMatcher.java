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
package org.movealong.sly.matchers.jdk;

import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.core.IsEqual.equalTo;

@AllArgsConstructor(access = PRIVATE)
public class ThrowableMessageMatcher extends TypeSafeDiagnosingMatcher<Throwable> {

    private final Matcher<? super CharSequence> messageMatcher;

    @Override
    public void describeTo(Description description) {
        description.appendText("exception with message ")
                   .appendDescriptionOf(messageMatcher);
    }

    @Override
    protected boolean matchesSafely(Throwable item, Description mismatchDescription) {
        boolean result = messageMatcher.matches(item.getMessage());
        if (!result) {
            mismatchDescription
                .appendText("message ")
                .appendDescriptionOf(d -> messageMatcher.describeMismatch(item.getMessage(), d));
        }

        return result;
    }

    public static Matcher<Throwable> hasMessageThat(Matcher<? super CharSequence> messageMatcher) {
        return new ThrowableMessageMatcher(messageMatcher);
    }

    public static Matcher<Throwable> hasMessageOf(String message) {
        return hasMessageThat(equalTo(message));
    }
}