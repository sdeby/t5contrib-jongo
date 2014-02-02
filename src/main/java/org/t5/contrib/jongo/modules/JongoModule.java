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

package org.t5.contrib.jongo.modules;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.LoggerSource;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.bson.types.ObjectId;
import org.t5.contrib.internal.jongo.JongoCrudRepositoryImpl;
import org.t5.contrib.internal.jongo.JongoServiceImpl;
import org.t5.contrib.jongo.JongoCollectionPackageManager;
import org.t5.contrib.jongo.JongoCrudRepository;
import org.t5.contrib.jongo.JongoCrudService;
import org.t5.contrib.jongo.JongoService;
import org.t5.contrib.jongo.encoders.JongoCollectionValueEncoder;

import java.util.Collection;
import java.util.Iterator;

/**
 * Module responsible for initializing MongoDB and supporting services
 */
public class JongoModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(JongoService.class, JongoServiceImpl.class);
        binder.bind(JongoCrudService.class);
        binder.bind(JongoCrudRepository.class, JongoCrudRepositoryImpl.class);
    }


    public static JongoCollectionPackageManager buildMongoCollectionPackageManager(
            final Collection<String> packageNames) {

        return new JongoCollectionPackageManager() {
            public Collection<String> getPackageNames() {
                return packageNames;
            }
        };
    }



    @Contribute(JongoCollectionPackageManager.class)
    public static void provideCollectionPackages(Configuration<String> configuration,

                                                 @Symbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM)
                                                 String appRootPackage) {
        configuration.add(appRootPackage + ".mongodb");
    }


    @Contribute(ValueEncoderSource.class)
    public static void provideEncoders(MappedConfiguration<Class, ValueEncoderFactory> configuration,
                                       final JongoService jongoService,
                                       final TypeCoercer typeCoercer, final PropertyAccess propertyAccess,
                                       final LoggerSource loggerSource) {
        Iterator<Class<?>> mappings = jongoService.getCollectionMappings().keySet().iterator();

        while (mappings.hasNext()) {
            final Class entityClass = mappings.next();

            if (entityClass != null) {
                ValueEncoderFactory factory = new ValueEncoderFactory() {
                    public ValueEncoder create(Class type) {
                        return new JongoCollectionValueEncoder(entityClass, jongoService, propertyAccess,
                                typeCoercer, loggerSource.getLogger(entityClass));
                    }
                };

                configuration.add(entityClass, factory);

            }
        }
    }

    @Contribute(TypeCoercer.class)
    public static void setupMongoDocumentTypeCoercer(Configuration<CoercionTuple> configuration) {
        configuration.add(new CoercionTuple(String.class, ObjectId.class,
                new Coercion<String, ObjectId>() {
                    /**
                     * Converts an input value.
                     *
                     * @param input the input value
                     */
                    public ObjectId coerce(String input) {
                        return ObjectId.massageToObjectId(input);
                    }
                }));
    }


}
