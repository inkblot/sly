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
package org.movealong.sly.model;

import org.junit.jupiter.api.Test;

import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iterates;
import static org.movealong.sly.model.Stringy.stringify;
import static org.movealong.sly.model.Stringy.stringy;

class StringyTest {
    @Test
    void fromString() {
        Stringy subject = stringy("foobar");
        assertThat(subject, iterates('f', 'o', 'o', 'b', 'a', 'r'));
        assertThat(subject.stringValue(), equalTo("foobar"));

        assertThat(stringy((CharSequence) subject), sameInstance(subject));
        assertThat(stringy((Iterable<Character>) subject), sameInstance(subject));
    }

    @Test
    void fromIterable() {
        Stringy subject = stringy(strictQueue('f', 'o', 'o', 'b', 'a', 'r'));
        assertThat(subject, iterates('f', 'o', 'o', 'b', 'a', 'r'));
        assertThat(subject.stringValue(), equalTo("foobar"));
    }

    @Test
    void stringifyCharSequence() {
        assertThat(stringify(new StringBuilder()
                                 .append('f')
                                 .append('o')
                                 .append('o')
                                 .append('b')
                                 .append('a')
                                 .append('r')),
                   equalTo("foobar"));
    }

    @Test
    void stringifyIterable() {
        assertThat(stringify(strictQueue('f', 'o', 'o', 'b', 'a', 'r')),
                   equalTo("foobar"));
    }

    @Test
    void idempotentConstruction() {
        Stringy subject = stringy("foobar");

        assertThat(stringy((CharSequence) subject), sameInstance(subject));
        assertThat(stringy((Iterable<Character>) subject), sameInstance(subject));
    }

    @Test
    void idempotentStringify() {
        String subject = "foobar";
        assertThat(stringify(subject), sameInstance(subject));
    }
}