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
package org.movealong.sly.lang.hfn;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.recursion.RecursiveResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.functions.Fn0.fn0;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Head.head;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Not.not;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Uncons.uncons;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Eq.eq;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.hfn.TrampolineM.trampolineM;

class TrampolineMTest {

    public static final Fn1<Map.Entry<String, Iterable<String>>, Maybe<RecursiveResult<Tuple2<String, Iterable<String>>, String>>> TRAMPOLINE_FN =
        into((head, tail) -> head(tail)
            .filter(not(eq(head)))
            .fmap(RecursiveResult::<Tuple2<String, Iterable<String>>, String>terminate)
            .catchError(fn0(() -> uncons(tail).fmap(RecursiveResult::recurse))));

    public static final TrampolineM<Maybe<?>, Tuple2<String, Iterable<String>>, String> SUT = trampolineM(TRAMPOLINE_FN);

    public static Stream<Arguments> lists() {
        return Stream.of(Arguments.of(strictQueue("duck", "duck", "goose"), just("goose")),
                         Arguments.of(strictQueue("duck", "duck", "duck", "duck"), Maybe.<String>nothing()));
    }

    @ParameterizedTest
    @MethodSource("lists")
    void trampoliningMaybe(Iterable<String> strings, Maybe<String> firstDifference) {
        Maybe<Tuple2<String, Iterable<String>>> subject = uncons(strings);
        assertEquals(firstDifference, SUT.apply(subject));
        assertEquals(subject.trampolineM(TRAMPOLINE_FN), SUT.apply(subject));
    }
}