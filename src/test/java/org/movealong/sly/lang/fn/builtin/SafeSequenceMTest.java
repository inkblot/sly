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

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functor.builtin.Identity.pureIdentity;
import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.pureReaderT;
import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.readerT;
import static com.jnape.palatable.lambda.monoid.Monoid.monoid;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.movealong.sly.lang.fn.builtin.SafeSequenceM.safeSequenceM;
import static testsupport.Constants.STACK_EXPLODING_NUMBER;

class SafeSequenceMTest {
    @Test
    void stackSafe() {
        Iterable<ReaderT<Unit, Identity<?>, Integer>> rs = replicate(STACK_EXPLODING_NUMBER, readerT(constantly(new Identity<>(1))));
        assertThat(monoid(Integer::sum, 0)
                           .reduceLeft(safeSequenceM(pureReaderT(pureIdentity()), rs)
                                               .<ReaderT<Unit, Identity<?>, Iterable<Integer>>>coerce()
                                               .<Identity<Iterable<Integer>>>runReaderT(UNIT)
                                               .runIdentity()),
                   equalTo(STACK_EXPLODING_NUMBER));
    }
}