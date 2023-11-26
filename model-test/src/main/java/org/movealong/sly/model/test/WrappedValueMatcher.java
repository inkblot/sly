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
package org.movealong.sly.model.test;

import lombok.AllArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.movealong.sly.model.WrappedValue;

import static lombok.AccessLevel.PROTECTED;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

@AllArgsConstructor(access = PROTECTED)
public abstract class WrappedValueMatcher<A, W extends WrappedValue<A>> extends TypeSafeDiagnosingMatcher<W> {

    private final Class<W>           type;
    private final Matcher<? super A> valueMatcher;

    @Override
    protected boolean matchesSafely(W item, Description mismatchDescription) {
        Matcher<Object> typeMatcher = instanceOf(type);
        if (!typeMatcher.matches(item)) {
            mismatchDescription.appendDescriptionOf(d -> typeMatcher.describeMismatch(item, d));
            return false;
        }
        mismatchDescription
            .appendText(type.getSimpleName())
            .appendText(" that ")
            .appendDescriptionOf(valueMatcher);
        if (!valueMatcher.matches(item.getValue())) {
            mismatchDescription
                .appendText(" but ")
                .appendDescriptionOf(d -> valueMatcher.describeMismatch(item.getValue(), d));
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(type.getSimpleName())
                   .appendText(" that ")
                   .appendDescriptionOf(valueMatcher);
    }
}
