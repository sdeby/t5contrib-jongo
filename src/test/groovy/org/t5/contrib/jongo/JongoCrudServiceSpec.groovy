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

package org.t5.contrib.jongo

import demo.model.User
import org.bson.types.ObjectId
import org.t5.contrib.jongo.JongoCrudService
import org.t5.contrib.jongo.JongoService
import specs.BaseSpecification
import spock.lang.Shared

class JongoCrudServiceSpec extends BaseSpecification {

    @Shared
    def jongoService, jongoCrudService

    def setupSpec() {
        jongoService = registry.getService(JongoService.class)
        jongoCrudService = registry.getService(JongoCrudService.class)
    }

    def "new document inserted in collection"() {

        when:

        jongoCrudService.create(new User("username", "password"))


        then:

        jongoService.findAll().size() == 1


    }

    def "existing document can be retrieved using proper query"() {

        when:

        jongoCrudService.create(new User("tapestry", "admin"))


        then:

        jongoCrudService.findOne(User.class, "{\"username\": #}", "tapestry") != null
    }


    def "existing document should update attributes"() {

        when:

        jongoCrudService.create(new User("user1", "pass1"))

        User user = jongoCrudService.findOne(User.class, "{\"username\": #}", "user1")

        // Now update
        user.setPassword("secret")

        jongoCrudService.update(user)

        then:

        jongoCrudService.findOne(User.class, "{\"password\": #}", "pass1") == null
        jongoCrudService.findOne(User.class, "{\"password\": #}", "secret") != null
    }


    def "should fail deleting non-existing document"() {

        when:

        def oid = "123456789"
        jongoCrudService.delete(User.class, new ObjectId(oid))



        then:
        IllegalArgumentException exception = thrown()
        exception.message == "invalid ObjectId [" + oid + "]"

    }

    def "should succeed deleting existing document"() {

        when:

        jongoCrudService.create(new User("admin", "adminPass"))

        User user = jongoCrudService.findOne(User.class, "{\"username\": #}", "admin")

        jongoCrudService.delete(User.class, user.getId())

        then:

        jongoCrudService.findOne(User.class, "{\"username\": #}", "admin") == null

    }


}