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

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;
import org.t5.contrib.jongo.JongoCrudRepository;
import org.t5.contrib.jongo.JongoService;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic MongoDB CRUD Service
 */
public class JongoCrudRepositoryImpl<T> implements JongoCrudRepository<T> {

    private final JongoService jongoService;
    private final MongoCollection collection;

    private final Class<T> clazz;

    public JongoCrudRepositoryImpl(JongoService jongoService, Class<T> clazz) {
        this.jongoService = jongoService;
        this.clazz = clazz;
        this.collection = jongoService.getCollection(getClazz());
    }

    private Class<T> getClazz() {
        return clazz;
    }

    @Override
    public T create(T t) {
        collection.insert(t);
        return t;
    }

    @Override
    public T get(ObjectId oid) {
        return collection.findOne("{_id: {$oid:#}}", oid).as(getClazz());
    }

    @Override
    public T update(T t) {
        collection.save(t);
        return t;
    }

    @Override
    public <T> void delete(ObjectId oid) {
        collection.remove(oid);

    }

    @Override
    public List<T> findAll(String query, Object... parameters) {
        Iterable<T> all = collection.find(query, parameters).as(getClazz());
        List<T> list = new ArrayList<>();
        for (T e : all) {
            list.add(e);
        }
        return list;

    }

    @Override
    public T findOne(String query, Object... parameters) {
        return collection.findOne(query, parameters).as(getClazz());
    }

    @Override
    public T update(T t, String query, Object... parameters) {
        collection.update(query, parameters).with(t);
        return t;
    }


}
