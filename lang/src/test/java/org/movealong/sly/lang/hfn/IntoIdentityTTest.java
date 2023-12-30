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

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.hfn.IntoIdentityT.intoIdentityT;
import static org.movealong.sly.lang.hfn.RunningIdentityT.runningIdentityT;

class IntoIdentityTTest {
    @Test
    void inversion() {
        Maybe<Identity<String>>            subject = just(new Identity<>("good"));
        IntoIdentityT<Maybe<?>, String>    sut     = intoIdentityT();
        RunningIdentityT<Maybe<?>, String> invert  = runningIdentityT();
        assertThat(sut.andThen(invert).apply(subject), equalTo(subject));
    }
}