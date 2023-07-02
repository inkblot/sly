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

import com.jnape.palatable.lambda.functions.Fn1;
import lombok.NoArgsConstructor;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

import static lombok.AccessLevel.PRIVATE;

/**
 * Captures the description of a {@link SelfDescribing} and returns it as a
 * <code>String</code> using its {@link SelfDescribing#describeTo} method and
 * a {@link StringDescription}.
 */
@NoArgsConstructor(access = PRIVATE)
public class DescriptionOf implements Fn1<SelfDescribing, String> {

    public static final DescriptionOf INSTANCE = new DescriptionOf();

    @Override
    public String checkedApply(SelfDescribing selfDescribing) {
        StringDescription description = new StringDescription();
        selfDescribing.describeTo(description);
        return description.toString();
    }

    public static DescriptionOf descriptionOf() {
        return INSTANCE;
    }

    public static String descriptionOf(SelfDescribing selfDescribing) {
        return descriptionOf().apply(selfDescribing);
    }
}
