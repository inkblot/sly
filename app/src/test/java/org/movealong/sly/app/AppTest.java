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

import com.jnape.palatable.lambda.io.IO;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.io.IO.io;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.movealong.sly.app.App.*;
import static org.movealong.sly.app.Runner.runner;
import static org.movealong.sly.app.Service.service;
import static org.movealong.sly.app.ServiceHandle.create;
import static testsupport.matchers.IOMatcher.yieldsValue;

class AppTest {

    @Test
    void runsAService() {
        ServiceHandle<Runner<String, IO<?>, IO<String>>> handle = create();

        assertThat(run(bind(handle, service(io(runner(() -> io("service")))))
                           .andThen(resolve(handle))),
                   yieldsValue(equalTo("service")));
    }

    @Test
    void runsAServiceWithDependencies() {
        ServiceHandle<String>                                 aKey           = create();
        ServiceHandle<String>                            bKey           = create();
        ServiceHandle<Runner<String, IO<?>, IO<String>>> producerHandle = create();

        assertThat(run(bind(aKey, service(io("composed")))
                           .andThen(bind(bKey, service(io("service"))))
                           .andThen(bind(producerHandle, service(
                               aKey, bKey, (a, b) -> service(io(runner(() -> io(a + " " + b)))))))
                           .andThen(resolve(producerHandle))),
                   yieldsValue(equalTo("composed service")));
    }

    @Test
    void detectsDuplicateBindings() {
        ServiceHandle<String>                            key            = create();
        ServiceHandle<Runner<String, IO<?>, IO<String>>> producerHandle = create();

        assertThrows(ServiceException.class,
                     () -> run(bind(key, service(io("first")))
                                   .andThen(bind(key, service(io("second"))))
                                   .andThen(bind(producerHandle, service(
                                       key, s -> service(io(runner(() -> io("resolved: %" + s)))))))
                                   .andThen(resolve(producerHandle))));
    }

    @Test
    void detectsResolutionCycles() {
        ServiceHandle<String>                                 aKey           = create();
        ServiceHandle<String>                            bKey           = create();
        ServiceHandle<Runner<String, IO<?>, IO<String>>> producerHandle = create();

        assertThrows(ServiceException.class,
                     () -> run(bind(aKey, service(bKey, b -> service(io("service A depends on " + b))))
                                   .andThen(bind(bKey, service(
                                       aKey, a -> service(io("service B depends on " + a)))))
                                   .andThen(bind(producerHandle, service(
                                       aKey, a -> service(io(runner(() -> io("resolved: " + a)))))))
                                   .andThen(resolve(producerHandle))));
    }

    @Test
    void detectsMissingDependency() {
        ServiceHandle<String>                                 aKey           = create();
        ServiceHandle<String>                            bKey           = create();
        ServiceHandle<Runner<String, IO<?>, IO<String>>> producerHandle = create();
        assertThrows(ServiceException.class,
                     () -> run(bind(aKey, service(io("composed")))
                                   .andThen(bind(producerHandle, service(
                                       aKey, bKey, (a, b) -> service(io(runner(() -> io(a + " " + b)))))))
                                   .andThen(resolve(producerHandle))));
    }
}