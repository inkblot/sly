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
package org.movealong.sly.concurrency;

import com.jnape.palatable.lambda.adt.hlist.HList;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.io.IO;
import org.junit.jupiter.api.Test;
import org.movealong.sly.jdk.Atom;

import java.util.concurrent.ExecutorService;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.Fn2.fn2;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Sort.sort;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Iterate.iterate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Sequence.sequence;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.jdk.Atom.atom;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iteratesAll;
import static testsupport.matchers.IOMatcher.yieldsValue;

class AtomTest {

    @Test
    void basicGetAndSet() {
        Atom<String> atom = atom("first");
        assertThat(atom.get(), yieldsValue(equalTo("first")));
        atom.set("second").unsafePerformIO();
        assertThat(atom.get(), yieldsValue(equalTo("second")));
    }

    @Test
    void multipleGet() {
        Atom<String> atom = atom("value");
        assertThat(atom.get().zip(atom.get().fmap(fn2(HList::tuple))),
                   yieldsValue(equalTo(tuple("value", "value"))));
    }

    @Test
    void parallelUpdates() throws Exception {
        final int             count    = 500_000;
        ExecutorService       executor = newCachedThreadPool();
        Fn1<Integer, Integer> inc      = i -> i + 1;

        Atom<Integer> atom  = atom(0);
        IO<Integer>   incUp = atom.update(inc.fmap(IO::io)).fmap(Tuple2::_1);

        assertThat(sequence(replicate(count, incUp), IO::io)
                       .fmap(sort())
                       .unsafePerformAsyncIO(executor)
                       .get(),
                   iteratesAll(take(count, iterate(inc, 0))));

        executor.shutdown();
    }
}