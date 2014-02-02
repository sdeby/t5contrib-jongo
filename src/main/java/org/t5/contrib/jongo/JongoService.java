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


import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.Map;

/**
 * Basic interface to interact with MongoDB collection via Jongo
 *
 */

public interface JongoService {


    /**
     * Retrieve a mongodb collection
     * @param collectionName
     * @return the collection
     */
    MongoCollection getCollection(String collectionName);


    /**
     * Retrieve a mongodb collection based on the class name.
     * The class is expected to be annotated with the <code>JongoCollection</code> annotation
     * @param clazz the collection class
     * @return the collection
     */
    MongoCollection getCollection(Class clazz);


    /**
     * Get a handle on the Jongo connection
     * @return the jongo connection
     */
    Jongo getJongo();

    /**
     * Mappings of classes and collection name
     * Note that only annotated classes are included
     *
     * @return the mapping of classes and collection names
     */
    Map<Class<?>, String> getCollectionMappings();
}
