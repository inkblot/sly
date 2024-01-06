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
package org.movealong.sly.lang.hfn;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.io.IO;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.monad.transformer.builtin.EitherT.eitherT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static testsupport.matchers.EitherMatcher.isLeftThat;
import static testsupport.matchers.EitherMatcher.isRightThat;
import static testsupport.matchers.IOMatcher.yieldsValue;

class RunningEitherTTest {
    @Test
    void runningRight() {
        assertThat(RunningEitherT.<IO<?>, String, Integer>runningEitherT()
                                 .<IO<Either<String, Integer>>>apply(eitherT(io(right(1)))),
                   yieldsValue(isRightThat(equalTo(1))));
    }

    @Test
    void runningLeft() {
        assertThat(RunningEitherT.<IO<?>, String, Integer>runningEitherT()
                                 .<IO<Either<String, Integer>>>apply(eitherT(io(left("no")))),
                   yieldsValue(isLeftThat(equalTo("no"))));
    }
}