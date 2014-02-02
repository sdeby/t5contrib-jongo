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

package org.t5.contrib.jongo.encoders;


import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.util.ExceptionUtils;
import org.slf4j.Logger;
import org.t5.contrib.jongo.JongoService;

/**
 *  Mongo implementation of value encoder
 */
public class JongoCollectionValueEncoder<T> implements ValueEncoder<T> {

    private final Class<T> colllectionClass;

    private final JongoService jongoService;

    private final TypeCoercer typeCoercer;

    private final PropertyAdapter propertyAdapter;

    private final Logger logger;

    public JongoCollectionValueEncoder(Class<T> rawType, JongoService jongoService,
                                       PropertyAccess propertyAccess, TypeCoercer typeCoercer, Logger logger) {
        this.colllectionClass = rawType;
        this.jongoService = jongoService;
        this.typeCoercer = typeCoercer;
        this.logger = logger;

        propertyAdapter = propertyAccess.getAdapter(this.colllectionClass).getPropertyAdapter("id");
    }

    public String toClient(T value) {
        if (value == null)
            return null;

        Object id = propertyAdapter.get(value);

        if (id == null) {
            return null;
        }

        return typeCoercer.coerce(id, String.class);
    }

    public T toValue(String clientValue) {
        if (InternalUtils.isBlank(clientValue))
            return null;

        Object id = null;

        try {
            id = typeCoercer.coerce(clientValue, propertyAdapter.getType());
        } catch (final Exception ex) {
            throw new RuntimeException(String.format(
                    "Exception converting '%s' to instance of %s (id type for entity %s): %s",
                    clientValue, propertyAdapter.getType().getName(), colllectionClass.getName(),
                    ExceptionUtils.toMessage(ex)), ex);
        }


        T result = jongoService.getCollection(colllectionClass)
                               .findOne("{_id:{$oid:#}}", id.toString())
                               .as(colllectionClass);

        if (result == null) {
            logger.error(String.format("Unable to convert client value '%s' into an entity instance.", clientValue));
        }

        return result;
    }
}