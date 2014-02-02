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

package org.t5.contrib.jongo;

import org.bson.types.ObjectId;

import java.util.List;


public interface JongoCrudRepository<T> {

    /**
     *
     * @param t collection object to persist
     * @return the persisted collection object
     */
     T create(T t);

    /**
     *
     * @param oid object identifier
     * @return the collection object
     */
     T get(ObjectId oid);

    /**
     * Update existing object
     * @param t the object to update
     * @return the collection object
     */
     T update(T t);

    /**
     * Deletes an object given an oid
     * @param oid  the object identifier
     * @param <T> object type
     */
     <T> void delete(ObjectId oid);

    /**
     *
     * @param query
     * @param parameters
     * @return the list of objects matching the search criteria
     */
     List<T> findAll(String query, Object... parameters);

    /**
     * @param query
     * @param parameters
     * @return an object matching the search criteria
     */
     T findOne(String query, Object... parameters);

    /**
     *
     * @param t
     * @param query
     * @param parameters
     * @return  the updated object
     */
     T update(T t, String query, Object... parameters);
}
