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
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.winterbourne.StreamT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Eq.eq;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into3.into3;
import static com.jnape.palatable.lambda.functions.builtin.fn2.LTE.lte;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.io.IO.pureIO;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static com.jnape.palatable.winterbourne.StreamT.empty;
import static com.jnape.palatable.winterbourne.StreamT.unfold;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.movealong.sly.lang.nt.MappingStreamT.mappingStreamT;
import static org.movealong.sly.matchers.lambda.StreamTMatcher.isEmptyStreamT;
import static org.movealong.sly.matchers.lambda.StreamTMatcher.streamsAll;
import static org.movealong.sly.test.lambda.YoloIO.yoloIO;

class MappingStreamTTest {
    @Test
    void mappedEmpty() {
        assertThat(MappingStreamT.mappingStreamT(yoloIO()).<Integer, StreamT<Identity<?>, Integer>>apply(empty(pureIO())),
                   isEmptyStreamT());
    }

    @Test
    void mappedWithEntries() {
        Iterable<Integer> integers = strictQueue(1, 1, 2, 3, 5);
        StreamT<IO<?>, Integer> fibonacciFive = unfold(
            into((i0, i1) -> io(just(i0).filter(lte(5)).fmap(i -> tuple(just(i), tuple(i1, i + i1))))),
            io(tuple(1, 1)));

        assertThat(mappingStreamT(yoloIO(), fibonacciFive),
                   streamsAll(integers));
    }

    @Test
    void mappingWithIntermittentEntries() {
        Iterable<Integer> integers = strictQueue(1, 2, 3, 5, 8);
        StreamT<IO<?>, Integer> intermittentFibonacci = unfold(
            into3((n, i0, i1) -> io(
                just(n).filter(lte(8))
                       .fmap(nn -> just(nn)
                           .filter(eq(i0))
                           .match(__ -> tuple(nothing(), tuple(nn + 1, i0, i1)),
                                  nnn -> tuple(just(nnn), tuple(nnn + 1, i1, i0 + i1)))))),
            io(tuple(1, 1, 2)));

        assertThat(mappingStreamT(yoloIO(), intermittentFibonacci),
                   streamsAll(integers));
    }
}