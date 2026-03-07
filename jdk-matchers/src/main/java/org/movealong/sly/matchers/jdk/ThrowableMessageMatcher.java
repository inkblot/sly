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

/**
 * Match a {@link Throwable} by its message.
 */
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

    /**
     * Creates a matcher that matches when the examined {@link Throwable} has a message
     * that satisfies the specified matcher.
     *
     * @param messageMatcher the matcher to apply to the message of the examined {@link Throwable}
     * @return a matcher that matches when the examined throwable has a message that satisfies the specified matcher
     */
    public static Matcher<Throwable> hasMessageThat(Matcher<? super CharSequence> messageMatcher) {
        return new ThrowableMessageMatcher(messageMatcher);
    }

    /**
     * Creates a matcher that matches when the examined {@link Throwable} has a message
     * that is equal to the specified string.
     *
     * @param message the string that the examined throwable's message is expected to equal
     * @return a matcher that matches when the examined throwable has a message equal to the specified string
     */
    public static Matcher<Throwable> hasMessageOf(String message) {
        return hasMessageThat(equalTo(message));
    }
}
