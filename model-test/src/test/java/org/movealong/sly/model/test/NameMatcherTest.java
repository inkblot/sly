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
package org.movealong.sly.model.test;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.movealong.sly.model.Label;
import org.movealong.sly.model.Name;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescribeMismatch.describeMismatch;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.model.Label.label;
import static org.movealong.sly.model.Name.name;
import static org.movealong.sly.model.test.NameMatcher.nameOf;
import static org.movealong.sly.model.test.NameMatcher.nameThat;

class NameMatcherTest {
    @Test
    void nameMatch() {
        Name        subject = name("xyzzy");
        NameMatcher sut     = nameOf("xyzzy");

        assertTrue(sut.matches(subject));
    }

    @Test
    void nameMismatch() {
        Name            subject      = name("blort");
        Matcher<String> valueMatcher = equalTo("xyzzy");
        NameMatcher     sut          = nameThat(valueMatcher);

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(containsString("Name that"),
                         containsString(descriptionOf(valueMatcher)),
                         containsString(describeMismatch(valueMatcher, "blort"))));
    }

    @Test
    void labelMismatch() {
        Label       subject = label("xyzzy");
        NameMatcher sut     = nameOf("xyzzy");

        assertFalse(sut.matches(subject));
        StringDescription mismatchDescription = new StringDescription();
        sut.describeMismatch(subject, mismatchDescription);
        assertThat(mismatchDescription.toString(),
                   allOf(containsString("is a"),
                         containsString("Label")));
    }
}