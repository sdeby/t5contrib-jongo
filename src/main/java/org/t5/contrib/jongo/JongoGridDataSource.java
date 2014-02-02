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

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Jongo based implementation of the GridDataSource
 */
public class JongoGridDataSource<T> implements GridDataSource {

    private final JongoService jongoService;

    private final Class<T> collectionType;

    private MongoCollection collection;


    private int startIndex;

    private List<T> preparedResults;

    public JongoGridDataSource(JongoService jongoService, Class<T> collectionType) {
        this.jongoService = jongoService;
        this.collectionType = collectionType;
        collection = jongoService.getCollection(collectionType);
    }

    public int getAvailableRows() {

        StringBuilder queryBuilder = new StringBuilder();


        List<Object> params = new ArrayList<>();


        applyAdditionalConstraints(queryBuilder, params);


        return (int) collection.count(queryBuilder.toString(), params.toArray());

    }

    public void prepare(int startIndex, int endIndex, List<SortConstraint> sortConstraints) {



        StringBuilder searchQuery = new StringBuilder();


        List<Object> params = new ArrayList<>();

        StringBuilder sortQuery = new StringBuilder();


        for (final SortConstraint constraint : sortConstraints) {

            final String propertyName = constraint.getPropertyModel().getPropertyName();

            switch (constraint.getColumnSort()) {

                case ASCENDING:
                    sortQuery.append(String.format("{\"%s\": 1}", propertyName));
                    break;

                case DESCENDING:
                    sortQuery.append(String.format("{\"%s\": -1}", propertyName));
                    break;

                default:
            }
        }

        applyAdditionalConstraints(searchQuery, params);

        this.startIndex = startIndex;

        Iterable<T> results = collection.find(searchQuery.toString(), params.toArray())
                                         .skip(startIndex)
                                         .limit(endIndex - startIndex + 1)
                                         .sort(sortQuery.toString())
                                         .as(collectionType);

        preparedResults = new ArrayList<T>();
        for (T t : results) {
            preparedResults.add(t);
        }
    }


    /**
     * Invoked after the main finder has been set up (firstResult, maxResults and any sort constraints). This gives
     * subclasses a chance to apply additional constraints before the list of results is obtained.
     * This implementation does nothing and may be overridden.
     */
    protected void applyAdditionalConstraints(StringBuilder searchBuilder, List<Object> parameters) {
    }

    public Object getRowValue(int index) {
        return preparedResults.get(index - startIndex);
    }

    public Class<T> getRowType() {
        return collectionType;
    }


}
