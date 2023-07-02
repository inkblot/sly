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

import com.jnape.palatable.lambda.io.IO;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.io.IO.throwing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.nt.PerformingIO.performingIO;
import static org.movealong.sly.matchers.lambda.TryMatcher.failedTryOf;
import static org.movealong.sly.matchers.lambda.TryMatcher.successfulTryThat;

class PerformingIOTest {

    @Test
    void safePerformIONormal() {
        IO<String> input = io("junit");
        assertThat(performingIO(input),
                   successfulTryThat(equalTo("junit")));
    }

    @Test
    void safePerformIOThrowing() {
        RuntimeException ex    = new RuntimeException("no");
        IO<String>       input = throwing(ex);
        assertThat(performingIO(input),
                   failedTryOf(ex));
    }
}