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

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn2.Sequence;
import com.jnape.palatable.lambda.functions.specialized.Pure;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.SafeT;
import lombok.NoArgsConstructor;

import static com.jnape.palatable.lambda.functions.builtin.fn2.$.$;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.monad.SafeT.pureSafeT;
import static lombok.AccessLevel.PRIVATE;
import static org.movealong.sly.lang.fn.builtin.SequenceM.sequenceM;

/**
 * Given an <code>Iterable</code> of {@link MonadRec}s and a pure function for the {@link MonadRec}, fold the elements
 * together using {@link Monad#flatMap(Fn1)} to produce <code>MonadRec&lt;Iterable&lt;A&gt;, M&gt;</code>.
 * <p>
 * This function is stack safe even with {@link MonadRec}s which are not stack safe under flatMap, but incurs the
 * overhead of {@link SafeT} to accomplish this. <code>SafeSequenceM</code> is also a drop-in replacement for
 * <code>SequnceM</code> for situations in which stack safe is necessary and possible.
 *
 * @param <A> the carrier type
 * @param <M> the {@link MonadRec} type
 *
 * @see Sequence
 * @see SequenceM
 */
@NoArgsConstructor(access = PRIVATE)
public final class SafeSequenceM<A, M extends MonadRec<?, M>> implements
        Fn2<Pure<M>, Iterable<? extends MonadRec<A, M>>, MonadRec<Iterable<A>, M>> {

    private static final SafeSequenceM<?, ?> INSTANCE = new SafeSequenceM<>();

    @Override
    public MonadRec<Iterable<A>, M> checkedApply(Pure<M> pureM, Iterable<? extends MonadRec<A, M>> imas) {
        return sequenceM(pureSafeT(pureM), map(SafeT::safeT, imas))
                .<SafeT<M, Iterable<A>>>coerce()
                .runSafeT();
    }

    @SuppressWarnings("unchecked")
    public static <A, M extends MonadRec<?, M>> SafeSequenceM<A, M> safeSequenceM() {
        return (SafeSequenceM<A, M>) INSTANCE;
    }

    public static <A, M extends MonadRec<?, M>> Fn1<Iterable<? extends MonadRec<A, M>>, MonadRec<Iterable<A>, M>>
    safeSequenceM(Pure<M> pureM) {
        return $(safeSequenceM(), pureM);
    }

    public static <A, M extends MonadRec<?, M>>
    MonadRec<Iterable<A>, M> safeSequenceM(Pure<M> pureM, Iterable<? extends MonadRec<A, M>> imas) {
        return $(safeSequenceM(pureM), imas);
    }
}
