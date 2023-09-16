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
package org.movealong.sly.hamcrest;

import org.hamcrest.SelfDescribing;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.hamcrest.IndentingDescription.indentWith;

class IndentingDescriptionTest {
    @Test
    void noEffectOnSingleLineDescriptions() {
        SelfDescribing sd = d -> d.appendText("Lorem ipsum dolor sit amet");

        assertThat(descriptionOf(indentWith("xyzzy", sd)), equalTo(descriptionOf(sd)));
    }

    @Test
    void indentationInsertedFollowingNewline() {
        SelfDescribing embedded = d -> d.appendText("not\nso\nsimple");
        SelfDescribing sd       = d -> d.appendText("complexity:\n\t").appendDescriptionOf(indentWith("\t", embedded));

        assertThat(descriptionOf(sd), equalTo("complexity:\n\tnot\n\tso\n\tsimple"));
    }
}