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

import com.jnape.palatable.traitor.annotations.TestTraits;
import com.jnape.palatable.traitor.framework.Subjects;
import com.jnape.palatable.traitor.runners.Traits;
import org.junit.runner.RunWith;
import testsupport.traits.FunctorLaws;
import testsupport.traits.TraversableLaws;

import static com.jnape.palatable.traitor.framework.Subjects.subjects;
import static org.movealong.sly.model.Label.label;
import static org.movealong.sly.model.Labeled.labeled;

@RunWith(Traits.class)
public class LabeledTest {

    @TestTraits(value = {
        FunctorLaws.class,
        TraversableLaws.class
    })
    public static Subjects<Labeled<Integer>> testSubjects() {
        return subjects(labeled(label("xyzzy"), 242));
    }
}