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
package org.movealong.sly.app;

import com.jnape.palatable.lambda.functor.builtin.Writer;
import com.jnape.palatable.shoki.impl.StrictQueue;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functor.builtin.Writer.tell;
import static com.jnape.palatable.lambda.functor.builtin.Writer.writer;
import static com.jnape.palatable.lambda.monoid.Monoid.monoid;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.movealong.sly.app.Starter.starter;
import static org.movealong.sly.app.Stopper.stopper;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iterates;

class StarterTest {

    @Test
    void startAndStopOrder() {
        assertThat(testStarter("outer")
                       .andThen(testStarter("inner"))
                       .<Writer<StrictQueue<String>, Stopper<Writer<StrictQueue<String>, ?>>>>start()
                       .discardR(tell(strictQueue("running")))
                       .flatMap(Stopper::stop)
                       .runWriter(monoid(StrictQueue::snocAll, strictQueue()))
                       ._2(),
                   iterates("starting outer",
                            "starting inner",
                            "running",
                            "stopping inner",
                            "stopping outer"));
    }

    private static Starter<Writer<StrictQueue<String>, ?>> testStarter(String name) {
        return starter(() -> writer(tuple(stopper(() -> writer(tuple(UNIT, strictQueue("stopping " + name)))),
                                          strictQueue("starting " + name))));
    }
}