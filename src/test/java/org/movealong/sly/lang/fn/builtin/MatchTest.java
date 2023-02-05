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

package org.movealong.sly.lang.fn.builtin;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.choice.Choice2.a;
import static com.jnape.palatable.lambda.adt.choice.Choice2.b;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.movealong.sly.lang.fn.builtin.Match.match;
import static testsupport.matchers.EitherMatcher.isRightThat;

class MatchTest {
    @Test
    void matchesA() {
        Object obj = new Object();

        Fn1<Choice2<Object, Object>, Either<AssertionError, Object>> sut =
                match(Either::right, o -> left(new AssertionError("Matched B, not A")));

        assertThat(sut.apply(a(obj)),
                   isRightThat(sameInstance(obj)));
    }

    @Test
    void matchesB() {
        Object obj = new Object();

        Fn1<Choice2<Object, Object>, Either<AssertionError, Object>> sut =
                match(o -> left(new AssertionError("Matched B, not A")), Either::right);

        assertThat(sut.apply(b(obj)),
                   isRightThat(sameInstance(obj)));
    }
}