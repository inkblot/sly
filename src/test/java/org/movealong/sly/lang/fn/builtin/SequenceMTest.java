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

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Size.size;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.io.IO.pureIO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.fn.builtin.SequenceM.sequenceM;
import static testsupport.Constants.STACK_EXPLODING_NUMBER;
import static testsupport.matchers.IOMatcher.yieldsValue;
import static testsupport.matchers.IterableMatcher.iterates;

class SequenceMTest {

    @Test
    void sequences() {
        assertThat(sequenceM(pureIO(), replicate(10, io(new AtomicInteger(0)::incrementAndGet))).coerce(),
                   yieldsValue(iterates(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
    }

    @Test
    void stackSafeWithSafeMonad() {
        assertThat(sequenceM(pureIO(), replicate(STACK_EXPLODING_NUMBER, io(1))).fmap(size()).coerce(),
                   yieldsValue(equalTo((long) STACK_EXPLODING_NUMBER)));
    }
}