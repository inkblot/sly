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
package org.movealong.sly.model;

import java.math.BigDecimal;

/**
 * The intent of <code>WrappedValue</code> is to help solve a specific common
 * application problem. In every application there are values that have
 * different semantics that are encoded using a common type, for example prices
 * and ratios that are both encoded using {@link BigDecimal}, or file paths and
 * usernames that are both encoded using {@link String}. The risk of expressing
 * semantically divergent values using the same type is that it requires
 * diligence to avoid using one where the other is appropriate, and no tooling
 * will help with this task.
 * <p>
 * In type-safe functional code, values with differing semantics are best
 * expressed using different types, enabling the compiler to enforce correct
 * usage. For many simple values, this is accomplished with a tiny type that
 * encapsulates a single value. Prices expressed using a <code>Price</code>
 * type that encapsulates a {@link BigDecimal} could not be used in a context
 * that expects <code>Ratio</code> without either raising a compiler error or a
 * deliberate choice to re-encapsulate the underlying {@link BigDecimal}.
 * <p>
 * In an application with many types like this, the <code>WrappedValue</code>
 * interface also provides a level of type unification so that, for example,
 * all single-member types that encapsulate a {@link String} could implement
 * <code>WrappedValue&lt;String&gt;</code>. Where generic value handling is
 * appropriate, it can be implemented against the common interface.
 *
 * @param <T> the wrapped type
 */
public interface WrappedValue<T> {
    T getValue();
}
