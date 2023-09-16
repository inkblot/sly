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
package org.movealong.sly.test.lambda;

import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class YoloIO implements NaturalTransformation<IO<?>, Identity<?>> {

    public static final YoloIO INSTANCE = new YoloIO();

    @Override
    public <A, GA extends Functor<A, Identity<?>>> GA apply(Functor<A, IO<?>> fa) {
        return new Identity<>(fa.<IO<A>>coerce().unsafePerformIO()).coerce();
    }

    public static NaturalTransformation<IO<?>, Identity<?>> yoloIO() {
        return INSTANCE;
    }
}
