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
package org.movealong.sly.lang.lens.shoki;

import com.jnape.palatable.shoki.impl.HashMap;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.optics.functions.Set.set;
import static com.jnape.palatable.lambda.optics.functions.View.view;
import static com.jnape.palatable.shoki.impl.HashMap.hashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.lens.shoki.MapLens.valueAt;

class MapLensTest {
    @Test
    void readFromEmpty() {
        assertEquals(view(valueAt("foo"), HashMap.<String, String>hashMap()),
                     nothing());
    }

    @Test
    void readFromAbsentKey() {
        assertEquals(view(valueAt("foo"), hashMap(tuple("bar", "baz"))),
                     nothing());
    }

    @Test
    void readFromPresentKey() {
        assertEquals(view(valueAt("foo"), hashMap(tuple("foo", "bar"))),
                     just("bar"));
    }

    @Test
    void setAbsentKey() {
        assertEquals(set(valueAt("foo"), just("bar"), hashMap()),
                     hashMap(tuple("foo", "bar")));
    }

    @Test
    void setPresentKey() {
        assertEquals(set(valueAt("foo"), just("baz"), hashMap(tuple("foo", "bar"))),
                     hashMap(tuple("foo", "baz")));
    }

    @Test
    void unsetPresentKey() {
        assertEquals(set(valueAt("foo"), nothing(), hashMap(tuple("foo", "bar"))),
                     HashMap.<String, String>hashMap());
    }
}