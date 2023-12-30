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
import com.jnape.palatable.lambda.functor.builtin.Identity;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.maybeT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.hfn.IntoMaybeT.intoMaybeT;
import static org.movealong.sly.lang.hfn.RunningMaybeT.runningMaybeT;

class IntoMaybeTTest {
    @Test
    void rightInversion() {
        Identity<Maybe<String>>            subject = new Identity<>(just("good"));
        IntoMaybeT<Identity<?>, String>    sut     = intoMaybeT();
        RunningMaybeT<Identity<?>, String> invert  = runningMaybeT();

        assertEquals(subject, sut.andThen(invert).apply(subject));
        assertEquals(maybeT(subject), invert.andThen(sut).apply(maybeT(subject)));
    }

    @Test
    void leftInversion() {
        Identity<Maybe<String>>            subject = new Identity<>(nothing());
        IntoMaybeT<Identity<?>, String>    sut     = intoMaybeT();
        RunningMaybeT<Identity<?>, String> invert  = runningMaybeT();

        assertEquals(subject, sut.andThen(invert).apply(subject));
        assertEquals(maybeT(subject), invert.andThen(sut).apply(maybeT(subject)));
    }
}