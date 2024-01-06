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
package org.movealong.sly.lang.hfn;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.jnape.palatable.winterbourne.StreamT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static com.jnape.palatable.winterbourne.StreamT.streamT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iterates;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustThat;
import static org.movealong.sly.test.lambda.Tuple2Matcher.isTuple2That;
import static testsupport.matchers.IOMatcher.yieldsValue;

class RunningStreamTTest {
    @Test
    void runningWithIO() {
        StreamT<IO<?>, Integer> subject = streamT(
            io(just(1)),
            io(nothing()),
            io(just(3)),
            io(nothing()),
            io(just(5)),
            io(nothing()),
            io(just(7)),
            io(nothing()),
            io(just(9)),
            io(nothing()));

        assertThat(RunningStreamT.<IO<?>, Integer>runningStreamT()
                                 .<MaybeT<IO<?>, Tuple2<Maybe<Integer>, StreamT<IO<?>, Integer>>>>apply(subject)
                                 .fmap(t -> t.<IO<StrictQueue<Maybe<Integer>>>>fmap(s -> s
                                     .fold((q, i) -> io(q.snoc(i)),
                                           io(strictQueue()))))
                                 .<IO<Maybe<Tuple2<Maybe<Integer>, IO<StrictQueue<Maybe<Integer>>>>>>>runMaybeT(),
                   yieldsValue(isJustThat(isTuple2That(
                       isJustOf(1),
                       yieldsValue(iterates(
                           nothing(),
                           just(3),
                           nothing(),
                           just(5),
                           nothing(),
                           just(7),
                           nothing(),
                           just(9),
                           nothing()))))));
    }
}