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
package org.movealong.sly.lang.nt;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.io.IO;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.monad.SafeT.safeT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static testsupport.matchers.IOMatcher.yieldsValue;

class RunningSafeTTest {
    @Test
    void runningWithIO() {
        assertThat(RunningSafeT
                       .<IO<?>>runningSafeT()
                       .apply(safeT(io("junit"))),
                   yieldsValue(equalTo("junit")));
    }

    @Test
    void runningWithPureMaybe() {
        assertThat(RunningSafeT
                       .<Maybe<?>>runningSafeT()
                       .apply(safeT(just("junit"))),
                   isJustOf("junit"));
    }

    @Test
    void runningWithMaybeInError() {
        assertThat(RunningSafeT
                       .<Maybe<?>>runningSafeT()
                       .apply(safeT(Maybe.<String>nothing())),
                   equalTo(Maybe.<String>nothing()));
    }
}