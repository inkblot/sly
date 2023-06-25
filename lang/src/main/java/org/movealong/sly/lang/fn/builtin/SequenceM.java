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
import com.jnape.palatable.shoki.impl.StrictQueue;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Upcast.upcast;
import static com.jnape.palatable.lambda.functions.builtin.fn2.$.$;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;

/**
 * Given an <code>Iterable</code> of {@link Monad}s and a pure function for the {@link Monad}, fold the elements
 * together using {@link Monad#flatMap(Fn1)} to produce <code>Monad&lt;Iterable&lt;A&gt;, M&gt;</code>.
 * <p>
 * This function is not safe in conjunction with {@link Monad}s that are not stack safe under flatMap. For these, use
 * {@link SafeSequenceM} instead if the {@link Monad} is also {@link MonadRec}.
 *
 * @param <A> the carrier type
 * @param <M> the {@link Monad} type
 *
 * @see Sequence
 * @see SafeSequenceM
 */
public class SequenceM<A, M extends Monad<?, M>> implements
        Fn2<Pure<M>, Iterable<? extends Monad<A, M>>, Monad<Iterable<A>, M>> {

    public static final SequenceM<?, ?> INSTANCE = new SequenceM<>();

    @Override
    public Monad<Iterable<A>, M> checkedApply(Pure<M> pureM, Iterable<? extends Monad<A, M>> mas) {
        return foldLeft((acc, ma) -> acc.flatMap(q -> ma.fmap(q::snoc)),
                        pureM.<StrictQueue<A>, Monad<StrictQueue<A>, M>>apply(strictQueue()),
                        mas)
                .fmap(upcast());
    }

    @SuppressWarnings("unchecked")
    public static <A, M extends Monad<?, M>> SequenceM<A, M> sequenceM() {
        return (SequenceM<A, M>) INSTANCE;
    }

    public static <A, M extends Monad<?, M>>
    Fn1<Iterable<? extends Monad<A, M>>, ? extends Monad<Iterable<A>, M>> sequenceM(Pure<M> pureM) {
        return $(sequenceM(), pureM);
    }

    public static <A, M extends Monad<?, M>> Monad<Iterable<A>, M>
    sequenceM(Pure<M> pureM, Iterable<? extends Monad<A, M>> mas) {
        return $(sequenceM(pureM), mas);
    }
}
