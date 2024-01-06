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
import org.movealong.sly.model.Label;

import static org.hamcrest.core.IsEqual.equalTo;

public final class LabelMatcher extends WrappedValueMatcher<String, Label> {
    private LabelMatcher(Matcher<? super String> valueMatcher) {
        super(Label.class, valueMatcher);
    }

    public static LabelMatcher labelThat(Matcher<? super String> valueMatcher) {
        return new LabelMatcher(valueMatcher);
    }

    public static LabelMatcher labelOf(String value) {
        return labelThat(equalTo(value));
    }
}
