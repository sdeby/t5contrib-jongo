// Copyright 2013 Serge Eby
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.t5.contrib.internal.jongo

import demo.model.Todo
import demo.model.User
import org.t5.contrib.jongo.JongoService
import specs.BaseSpecification
import spock.lang.Shared


class JongoServiceImplSpec extends BaseSpecification {

    @Shared
    def jongoService

    def setupSpec() {
        jongoService = registry.getService(JongoService.class)
    }

    def "should return the appropriate collection name"() {
        expect:

        name == jongoService.getCollection(clazz).name

        where:

        name    | clazz
        "users" | User.class
        "todos" | Todo.class
    }

    def "annotated classes properly detected"() {

        expect:

        jongoService.collectionMappings.size() == 2
    }


    def "non-existing collection raises execption"() {

        def collectionName = "collectionDoesNotExist"

        when:

        jongoService.getCollection(collectionName)


        then:

        RuntimeException e = thrown()
        e.message == "Collection $collectionName doesn't appear to be applied to any annotated class"

    }

    def "null collection name raises exception"() {

        when:

        jongoService.getCollection(null)

        then:
        RuntimeException e = thrown()
        e.message == "Collection name cannot be null"

    }

}