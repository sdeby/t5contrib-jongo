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

package org.t5.contrib.internal.jongo;


import org.apache.tapestry5.ioc.services.ClassNameLocator;
import org.apache.tapestry5.mongodb.MongoDB;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.t5.contrib.jongo.JongoCollectionPackageManager;
import org.t5.contrib.jongo.JongoService;
import org.t5.contrib.jongo.annotations.JongoCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link org.t5.contrib.jongo.JongoService}
 */
public class JongoServiceImpl implements JongoService {
    private final MongoDB mongoDB;
    private final Jongo jongo;
    private final JongoCollectionPackageManager packageManager;

    private final ClassNameLocator classNameLocator;

    private Map<Class<?>, String> collectionMappings;

    public JongoServiceImpl(MongoDB mongoDB,
                            JongoCollectionPackageManager packageManager,
                            ClassNameLocator classNameLocator) {

        this.mongoDB = mongoDB;
        this.packageManager = packageManager;
        this.classNameLocator = classNameLocator;
        jongo = new Jongo(mongoDB.getDefaultMongoDb());

        collectionMappings = new HashMap<>();

        for (String packageName : this.packageManager.getPackageNames()) {
            for (String className : this.classNameLocator.locateClassNames(packageName)) {

                Class<?> clazz = loadClass(className);

                if (clazz.isAnnotationPresent(JongoCollection.class)) {
                    String collectionName = clazz.getAnnotation(JongoCollection.class).name();
                    collectionMappings.put(clazz, collectionName);
                }
            }
        }
    }

    public MongoCollection getCollection(String collectionName) {
        if (collectionName == null) {
            throw new RuntimeException("Collection name cannot be null");
        }

        if (!collectionMappings.containsValue(collectionName)) {
            throw new RuntimeException(String.format("Collection %s doesn't appear to be applied to any annotated class",
                    collectionName));
        }

        return jongo.getCollection(collectionName);
    }

    public MongoCollection getCollection(Class clazz) {
        return this.getCollection(collectionMappings.get(clazz));
    }

    public Jongo getJongo() {
        return jongo;
    }

    public Map<Class<?>, String> getCollectionMappings() {
        return collectionMappings;
    }

    private Class loadClass(final String className) {
        try {
            return Thread.currentThread()
                    .getContextClassLoader().loadClass(className);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
