/*
 * Copyright (c) 2025 Nate Riffe
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

/**
 * The {@link SemanticError} type is a wrapper around {@link Failures} which is
 * meant to add semantic meaning to a {@link Failures}. Operations with
 * different failure modes that might require different handling are expected
 * to implement a subtype of {@link SemanticError} that algebraically encodes
 * the distinct failure modes. The {@link SemanticError} type exists to enforce
 * that these semantic coproducts are meant to wrap an instance of
 * {@link Failures}.
 */
public interface SemanticError extends WrappedValue<Failures> {
    /**
     * The {@link Failures} that this {@link SemanticError} wraps.
     *
     * @return the wrapped {@link Failures}
     */
    Failures getFailures();

    /**
     * {@inheritDoc}
     */
    @Override
    default Failures getValue() {
        return getFailures();
    }
}
