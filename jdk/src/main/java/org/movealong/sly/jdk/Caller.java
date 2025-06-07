/*
 * Copyright (c) 2024-2025 Nate Riffe
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
package org.movealong.sly.jdk;

import lombok.NoArgsConstructor;

/**
 * Utility class for collecting information about the calling context.
 */
@NoArgsConstructor
public class Caller {

    /**
     * Returns the caller. This method returns the frame that is two steps up
     * the stack from its own stack frame. In typical usage, a method can know
     * its caller by calling this method. As a stack-sensitive method, usage in
     * a lambda will produce the stack frame that invoked the lambda, rather
     * than the stack frame of the method that produced the lambda. Deferral in
     * this manner may be desirable, but also easy to accidentally introduce.
     *
     * @return the stack frame from which the method calling caller was invoked
     */
    public static StackWalker.StackFrame caller() {
        return StackWalker.getInstance().walk(s -> s.skip(2).findFirst().orElseThrow());
    }
}
