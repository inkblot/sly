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

import lombok.Getter;

/**
 * An exception that is thrown in the course of binding or resolving services
 * in {@link App}.
 */
@Getter
public class ServiceException extends Exception {
    private final ServiceHandle<?> serviceHandle;

    public ServiceException(String message, ServiceHandle<?> serviceHandle) {
        super(message);
        this.serviceHandle = serviceHandle;
    }
}
