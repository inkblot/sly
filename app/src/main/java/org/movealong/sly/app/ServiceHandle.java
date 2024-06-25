/*
 * Copyright (c) 2024 Nate Riffe
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
package org.movealong.sly.app;

import com.jnape.palatable.lambda.adt.hmap.TypeSafeKey;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

/**
 * A handle with non-fungible values that is used for binding {@link Service}s
 * in an {@link App}. Each {@link Service} that is bound in an {@link App}
 * should have its own <code>ServiceHandle</code> that other {@link Service}s
 * can use to wire the associated {@link Service}.
 *
 * @param <S> the service type
 */
@ToString
@RequiredArgsConstructor(access = PRIVATE)
public final class ServiceHandle<S> implements TypeSafeKey.Simple<Service<S>> {
    private final String initializedAt;

    public static <S> ServiceHandle<S> create() {
        return new ServiceHandle<>(StackWalker.getInstance().walk(s -> s.skip(1).findFirst().map(Objects::toString).orElse("unknown")));
    }
}
