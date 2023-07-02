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

import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.functor.builtin.State;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.monad.SafeT;
import com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functor.builtin.Identity.pureIdentity;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.jnape.palatable.lambda.monad.SafeT.pureSafeT;
import static com.jnape.palatable.lambda.monad.SafeT.safeT;
import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.pureReaderT;
import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.readerT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.movealong.sly.lang.fn.builtin.SequenceM.sequenceM;
import static org.movealong.sly.lang.nt.MappingSafeT.mappingSafeT;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iteratesAll;
import static testsupport.Constants.STACK_EXPLODING_NUMBER;

class MappingSafeTTest {

    private static <S> NaturalTransformation<State<S, ?>, ReaderT<S, Identity<?>, ?>> evaluatingState() {
        return new NaturalTransformation<State<S, ?>, ReaderT<S, Identity<?>, ?>>() {
            @Override
            public <A, GA extends Functor<A, ReaderT<S, Identity<?>, ?>>> GA apply(Functor<A, State<S, ?>> fa) {
                return readerT((S i) -> new Identity<>(fa.<State<S, A>>coerce().eval(i))).coerce();
            }
        };
    }

    @Test
    void stillStackSafeAfterMapping() {
        Monad<Iterable<Integer>, SafeT<ReaderT<Integer, Identity<?>, ?>, ?>> manyReaderTs =
            sequenceM(pureSafeT(pureReaderT(pureIdentity())),
                      replicate(STACK_EXPLODING_NUMBER,
                                mappingSafeT(MappingSafeTTest.<Integer>evaluatingState())
                                    .<Integer, SafeT<ReaderT<Integer, Identity<?>, ?>, Integer>>apply(
                                        safeT(state(i -> tuple(i, i + 1))))));
        assertThat(manyReaderTs
                       .<SafeT<ReaderT<Integer, Identity<?>, ?>, Iterable<Integer>>>coerce()
                       .<ReaderT<Integer, Identity<?>, Iterable<Integer>>>runSafeT()
                       .<Identity<Iterable<Integer>>>runReaderT(0)
                       .runIdentity(),
                   iteratesAll(replicate(STACK_EXPLODING_NUMBER, 0)));
    }
}