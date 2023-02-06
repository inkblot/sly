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

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.optics.Lens;
import com.jnape.palatable.shoki.api.Map;
import lombok.NoArgsConstructor;

import static com.jnape.palatable.lambda.functions.Fn0.fn0;
import static com.jnape.palatable.lambda.optics.Lens.simpleLens;
import static lombok.AccessLevel.PRIVATE;

/**
 * Lens for {@link Map}.
 */
@NoArgsConstructor(access = PRIVATE)
public final class MapLens {

    /**
     * A lens that focuses on a value in a {@link Map} as a {@link Maybe}.
     *
     * @param key    the key to focus on
     * @param <Size> the known size <code>Number</code> type of the {@link Map}
     * @param <K>    the key type of the {@link Map}
     * @param <V>    the value type of the {@link Map}
     * @return the value that may be in the {@link Map} at the given key
     */
    public static <Size extends Number, K, V> Lens.Simple<Map<Size, K, V>, Maybe<V>> valueAt(K key) {
        return simpleLens(m -> m.get(key),
                          (m, nv) -> nv.match(fn0(() -> m.remove(key)),
                                              v -> m.put(key, v)));
    }

    /**
     * A function that given a key produces a lens that focuses on a value in a {@link Map} as a {@link Maybe}.
     *
     * @param <Size> the known size <code>Number</code> type of the {@link Map}
     * @param <K>    the key type of the {@link Map}
     * @param <V>    the value type of the {@link Map}
     * @return the function from <code>K</code> to <code>Lens.Simple&lt;Map&lt;Size, K, V&gt;, Maybe&lt;V&gt;&gt;</code>
     */
    public static <Size extends Number, K, V> Fn1<K, Lens.Simple<Map<Size, K, V>, Maybe<V>>> valueAt() {
        return MapLens::valueAt;
    }
}
