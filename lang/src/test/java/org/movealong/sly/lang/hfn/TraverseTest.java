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

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.hfn.Traverse.traverse;

class TraverseTest {
    @Test
    void eitherMaybeVariations() {
        HyperFn<Either<Throwable, ?>, Maybe<String>, Maybe<?>, Either<Throwable, String>> sut =
            traverse(id(), Maybe::just);

        assertEquals(sut.apply(right(just("foo"))), just(right("foo")));
        assertEquals(sut.apply(right(nothing())), nothing());
        RuntimeException x = new RuntimeException();
        assertEquals(sut.apply(left(x)), just(left(x)));
    }

    @Test
    void tupleIdentityVariations() {
        HyperFn<Tuple2<String, ?>, Identity<String>, Identity<?>, Tuple2<String, String>> sut =
            traverse(id(), Identity::new);

        assertEquals(sut.apply(tuple("foo", new Identity<>("bar"))), new Identity<>(tuple("foo", "bar")));
    }
}