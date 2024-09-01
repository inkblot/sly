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
package org.movealong.sly.model;

import org.junit.jupiter.api.Test;
import org.movealong.sly.model.Failures.Ascribed;
import org.movealong.sly.model.Failures.Multiple;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Both.both;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iterates;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iteratesItemsThat;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustThat;
import static org.movealong.sly.model.Failures.message;
import static org.movealong.sly.model.Label.label;
import static org.movealong.sly.test.lambda.Tuple2Matcher.isTuple2That;

class FailuresTest {
    @Test
    void aggregates() {
        assertThat(message("whoopie")
                       .add(message("daisy"))
                       .projectC()
                       .fmap(Multiple::getFailures),
                   isJustThat(iterates(message("whoopie"),
                                       message("daisy"))));
    }

    @Test
    void aggregatesCommutativity() {
        assertThat(message("first")
                       .add(message("second"))
                       .add(message("third"))
                       .projectC()
                       .fmap(Multiple::getFailures),
                   isJustThat(iterates(message("first"),
                                       message("second"),
                                       message("third"))));

        assertThat(message("first")
                       .add(message("second")
                                .add(message("third")))
                       .projectC()
                       .fmap(Multiple::getFailures),
                   isJustThat(iterates(message("first"),
                                       message("second"),
                                       message("third"))));
    }

    @Test
    void ascription() {
        assertThat(message("required value not present")
                       .ascribe(label("fieldName"))
                       .projectD()
                       .fmap(a -> tuple(a.getAscription(), a.getFailures())),
                   isJustThat(isTuple2That(
                       equalTo(label("fieldName")),
                       equalTo(message("required value not present")))));
    }

    @Test
    void ascribedAggregate() {
        assertThat(message("whoopsie")
                       .add(message("daisy"))
                       .ascribe(label("cause"))
                       .projectD()
                       .fmap(both(Ascribed::getAscription,
                                  a -> a.getFailures()
                                        .projectC()
                                        .fmap(Multiple::getFailures))),
                   isJustThat(isTuple2That(equalTo(label("cause")),
                                           isJustThat(iterates(message("whoopsie"), message("daisy"))))));
    }

    @Test
    void aggregatedAscribed() {
        assertThat(message("whoopsie")
                       .ascribe(label("cause1"))
                       .add(message("daisy")
                                .ascribe(label("cause2")))
                       .projectC()
                       .fmap(Multiple::getFailures)
                       .fmap(map(Failures::projectD))
                       .fmap(map(m -> m.fmap(a -> tuple(a.getAscription(), a.getFailures())))),
                   isJustThat(iteratesItemsThat(
                       isJustThat(isTuple2That(equalTo(label("cause1")),
                                               equalTo(message("whoopsie")))),
                       isJustThat(isTuple2That(equalTo(label("cause2")),
                                               equalTo(message("daisy")))))));
    }
}