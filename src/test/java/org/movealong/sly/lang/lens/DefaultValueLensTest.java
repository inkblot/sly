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
package org.movealong.sly.lang.lens;

import com.jnape.palatable.lambda.optics.Lens;
import com.jnape.palatable.shoki.api.Map;
import com.jnape.palatable.shoki.api.Natural;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.optics.functions.Set.set;
import static com.jnape.palatable.lambda.optics.functions.View.view;
import static com.jnape.palatable.shoki.impl.HashMap.hashMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.lens.DefaultValueLens.defaultValue;
import static org.movealong.sly.lang.lens.shoki.MapLens.valueAt;

class DefaultValueLensTest {
    @Test
    void viewJustProducesValue() {
        assertThat(view(defaultValue(-1), just(1)), equalTo(1));
    }

    @Test
    void viewNothingProducesDefault() {
        assertThat(view(defaultValue(-1), nothing()), equalTo(-1));
    }

    @Test
    void setJustReplacesValue() {
        assertThat(set(defaultValue(-1), 1, just(0)), equalTo(just(1)));
    }

    @Test
    void setNothingPreservesValue() {
        assertThat(set(defaultValue(-1), 1, nothing()), equalTo(just(1)));
    }

    @Test
    void unLiftMapLens() {
        Lens<Map<Natural, String, String>, Map<Natural, String, String>, String, String> sut =
                defaultValue(valueAt("foo"), "bar");

        assertEquals(view(sut, hashMap()), "bar");
        assertEquals(view(sut, hashMap(tuple("foo", "bar"))), "bar");
        assertEquals(view(sut, hashMap(tuple("foo", "baz"))), "baz");
        assertEquals(set(sut, "bar", hashMap()), hashMap(tuple("foo", "bar")));
    }
}