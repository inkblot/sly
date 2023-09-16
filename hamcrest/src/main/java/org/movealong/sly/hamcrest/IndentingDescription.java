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

import lombok.AllArgsConstructor;
import org.hamcrest.BaseDescription;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

import static lombok.AccessLevel.PRIVATE;

/**
 * An implementation of {@link Description} that wraps a delegate and causes
 * each new line of the delegate's output to be indented using the supplied
 * string value.
 */
@AllArgsConstructor(access = PRIVATE)
public class IndentingDescription extends BaseDescription {

    private final String      indent;
    private final Description delegate;

    @Override
    protected void append(char c) {
        delegate.appendText(Character.toString(c));
        if ('\n' == c) {
            delegate.appendText(indent);
        }
    }

    public static Description indentWith(String indent, Description delegate) {
        return new IndentingDescription(indent, delegate);
    }

    public static SelfDescribing indentWith(String indent, SelfDescribing delegate) {
        return description -> delegate.describeTo(indentWith(indent, description));
    }
}
