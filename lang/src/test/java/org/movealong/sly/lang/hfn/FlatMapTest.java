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
import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Not.not;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Eq.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.hfn.FlatMap.flatMap;

class FlatMapTest {

    @Test
    void presentToPresentMaybe() {
        Maybe<String>                                subject = just("this is a test");
        Fn1<String, Maybe<Integer>>                  fn      = indexOf("test");
        HyperFn<Maybe<?>, String, Maybe<?>, Integer> sut     = flatMap(fn);
        assertEquals(subject.flatMap(fn), sut.apply(subject));
    }

    @Test
    void presentToAbsentMaybe() {
        Maybe<String>                                subject = just("this is a test");
        Fn1<String, Maybe<Integer>>                  fn      = indexOf("trial");
        HyperFn<Maybe<?>, String, Maybe<?>, Integer> sut     = flatMap(fn);
        assertEquals(subject.flatMap(fn), sut.apply(subject));
    }

    private static Fn1<String, Maybe<Integer>> indexOf(String substring) {
        return s -> just(s.indexOf(substring)).filter(not(eq(-1)));
    }
}