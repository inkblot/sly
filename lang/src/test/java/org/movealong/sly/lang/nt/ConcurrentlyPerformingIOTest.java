/*
 * Copyright (c) 2024 Nate Riffe
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

import com.jnape.palatable.lambda.adt.Try;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.io.IO.throwing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.movealong.sly.lang.nt.ConcurrentlyPerformingIO.concurrentlyPerformingIO;
import static org.movealong.sly.matchers.lambda.TryMatcher.failedTryThat;

class ConcurrentlyPerformingIOTest {

    static class TestException extends RuntimeException {}

    @Test
    void unwrapsExceptions() {
        assertThat(concurrentlyPerformingIO()
                       .<String, Try<String>>apply(throwing(new TestException())),
                   failedTryThat(instanceOf(TestException.class)));
    }
}