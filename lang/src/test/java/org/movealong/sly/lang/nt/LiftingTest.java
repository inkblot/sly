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
import com.jnape.palatable.lambda.monad.transformer.builtin.EitherT;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.monad.transformer.builtin.EitherT.eitherT;
import static com.jnape.palatable.lambda.monad.transformer.builtin.EitherT.liftEitherT;
import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.liftMaybeT;
import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.maybeT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.nt.Lifting.lifting;

class LiftingTest {
    @Test
    void liftsEitherT() {
        Lifting<EitherT<?, String, ?>, Identity<?>, EitherT<Identity<?>, String, ?>> sut = lifting(liftEitherT());

        assertThat(sut.<Integer, EitherT<Identity<?>, String, Integer>>apply(new Identity<>(0)),
                   equalTo(eitherT(new Identity<>(right(0)))));
    }

    @Test
    void liftsMaybeT() {
        Lifting<MaybeT<?, ?>, Identity<?>, MaybeT<Identity<?>, ?>> sut = lifting(liftMaybeT());

        assertThat(sut.apply(new Identity<>(0)),
                   equalTo(maybeT(new Identity<>(just(0)))));
    }
}