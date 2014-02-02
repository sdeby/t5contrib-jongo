package org.t5.contrib.jongo;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic MongoDB CRUD Service for low-level interaction
 */
public class JongoCrudService {
    private final JongoService jongoService;
    private String collectionName;
    private MongoCollection collection;


    public JongoCrudService(JongoService jongoService) {
        this.jongoService = jongoService;
    }

    /**
     * Create a new document
     *
     * @param t the object to create
     * @param <T> the type of object to create
     * @return the newly created document instance
     */
    public <T> T create(T t) {
        collection = jongoService.getCollection(t.getClass());
        collection.insert(t);
        return t;
    }


    /**
     * Return a document by Id
     *
     * @param <T>  object type
     * @param clazz the model class
     * @param oid   the object id
     * @return the document
     */
    public <T> T get(Class<T> clazz, ObjectId oid) {
        collection = jongoService.getCollection(clazz);
        return collection.findOne(oid).as(clazz);
    }

    /**
     * Update a document
     *
     * @param <T> object type 
     * @param t document to be updated
     * @return the updated document
     */
    public <T> T update(T t) {
        collection = jongoService.getCollection(t.getClass());
        collection.save(t);
        return t;
    }

    /**
     * Delete a document
     *
     * @param <T>   object type 
     * @param clazz model  class
     * @param oid   the document ID
     */
    public <T> void delete(Class<T> clazz, ObjectId oid) {
        collection = jongoService.getCollection(clazz);
        collection.remove(oid);

    }


    /**
     * Get the list of documents matching filtering criteria
     *
     * @param <T>        object type
     * @param clazz      model class
     * @param query      query string for filtering
     * @param parameters query parameters
     * @return the list of documents
     */
    public <T> List<T> findAll(Class<T> clazz, String query, Object... parameters) {
        collection = jongoService.getCollection(clazz);
        Iterable<T> all = collection.find(query, parameters).as(clazz);
        List<T> list = new ArrayList<T>();
        for (T e : all) {
            list.add(e);
        }
        return list;

    }

    /**
     * Get the document matching filtering criteria
     *
     * @param <T>        object type
     * @param clazz      model class
     * @param query      query string for filtering
     * @param parameters query parameters
     * @return the document
     */
    public <T> T findOne(Class<T> clazz, String query, Object... parameters) {
        collection = jongoService.getCollection(clazz);
        return collection.findOne(query, parameters).as(clazz);

    }

    /**
     * Update a document matching filtering criteria
     *
     * @param <T>        object type
     * @param t          document to be updated
     * @param query      query string for filtering
     * @param parameters query parameters
     * @return the updated document
     */
    public <T> T update(T t, String query, Object... parameters) {
        collection = jongoService.getCollection(t.getClass());
        collection.update(query, parameters);
        return t;
    }

}
