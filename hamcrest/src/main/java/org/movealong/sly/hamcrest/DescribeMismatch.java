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
package org.movealong.sly.hamcrest;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import lombok.NoArgsConstructor;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import static lombok.AccessLevel.PRIVATE;

/**
 * Captures the description of a mismatch between the expectations set by a
 * {@link Matcher} and a value, returning it as a <code>String</code>.
 *
 * @param <A> The value type
 */
@NoArgsConstructor(access = PRIVATE)
public class DescribeMismatch<A> implements Fn2<Matcher<? super A>, A, String> {

    private static final DescribeMismatch<?> INSTANCE = new DescribeMismatch<>();

    @Override
    public String checkedApply(Matcher<? super A> matcher, A a) {
        StringDescription mismatchDescription = new StringDescription();
        matcher.describeMismatch(a, mismatchDescription);
        return mismatchDescription.toString();
    }

    @SuppressWarnings("unchecked")
    public static <A> DescribeMismatch<A> describeMismatch() {
        return (DescribeMismatch<A>) INSTANCE;
    }

    public static <A> Fn1<A, String> describeMismatch(Matcher<? super A> matcher) {
        return DescribeMismatch.<A>describeMismatch().apply(matcher);
    }

    public static <A> String describeMismatch(Matcher<? super A> matcher, A a) {
        return DescribeMismatch.<A>describeMismatch(matcher).apply(a);
    }
}
