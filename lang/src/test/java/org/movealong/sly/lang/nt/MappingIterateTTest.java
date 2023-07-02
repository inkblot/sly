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

import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.monad.transformer.builtin.IterateT;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Iterate.iterate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.io.IO.pureIO;
import static com.jnape.palatable.lambda.monad.transformer.builtin.IterateT.empty;
import static com.jnape.palatable.lambda.monad.transformer.builtin.IterateT.fromIterator;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.movealong.sly.lang.nt.MappingIterateT.mappingIterateT;
import static org.movealong.sly.test.lambda.YoloIO.yoloIO;
import static testsupport.Constants.STACK_EXPLODING_NUMBER;
import static testsupport.matchers.IterateTMatcher.isEmpty;
import static testsupport.matchers.IterateTMatcher.iteratesAll;

class MappingIterateTTest {
    @Test
    void mappingIterateTEmpty() {
        assertThat(mappingIterateT(yoloIO())
                       .<Integer, IterateT<Identity<?>, Integer>>apply(empty(pureIO())),
                   isEmpty());
    }

    @Test
    void mappingIterateTNonEmpty() {
        List<Integer> integers = asList(1, 1, 2, 3, 5);
        assertThat(mappingIterateT(yoloIO())
                       .apply(fromIterator(integers.iterator())),
                   iteratesAll(integers));
    }

    @Test
    void stackSafe() {
        Iterable<Integer> numbers = take(STACK_EXPLODING_NUMBER,
                                         iterate(x -> x + 1, 0));
        assertThat(mappingIterateT(yoloIO()).apply(fromIterator(numbers.iterator())),
                   iteratesAll(numbers));
    }
}