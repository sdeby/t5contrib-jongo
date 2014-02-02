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

package specs

import org.apache.tapestry5.internal.InternalConstants
import org.apache.tapestry5.ioc.*
import org.apache.tapestry5.ioc.annotations.Contribute
import org.apache.tapestry5.ioc.annotations.Local
import org.apache.tapestry5.ioc.services.ApplicationDefaults
import org.apache.tapestry5.ioc.services.ServiceOverride
import org.apache.tapestry5.ioc.services.SymbolProvider
import org.apache.tapestry5.ioc.services.SymbolSource
import org.apache.tapestry5.modules.TapestryModule
import org.apache.tapestry5.mongodb.MongoDBSource
import org.apache.tapestry5.mongodb.MongoDBSymbols
import org.apache.tapestry5.mongodb.modules.MongodbModule
import org.t5.contrib.internal.jongo.EmbeddedMongoDBSource
import org.t5.contrib.jongo.JongoCollectionPackageManager
import org.t5.contrib.jongo.modules.JongoModule
import spock.lang.Shared
import spock.lang.Specification

/**
 * Base spec class that provides the necessary to load tapestry services
 *
 */

abstract class BaseSpecification extends Specification {

    /**
     * Use this registry to get access to Tapestry services
     */
    @Shared
    protected Registry registry


    def setupSpec() {

        registry = new RegistryBuilder().add(

                TapestryModule.class,

                MongodbModule.class,

                JongoModule.class,

                IntegrationTestModule.class)

                .build()

        registry.performRegistryStartup()

    }


    def cleanupSpec() {

        if (registry != null) {
            registry.shutdown()
        }

    }


    static class IntegrationTestModule {
        public static void bind(ServiceBinder binder) {
            binder.bind(MongoDBSource.class, EmbeddedMongoDBSource.class).withId("EmbeddedMongo")
        }


        @Contribute(SymbolSource.class)
        public static void setupSymbolProviders(OrderedConfiguration<SymbolProvider> configuration) {
            //fails without tapestry.app-name symbol defined
            configuration.add("AppPackage", new SymbolProvider() {
                public String valueForSymbol(String symbolName) {
                    if (symbolName.equalsIgnoreCase(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM))
                        return "demo"
                    return null
                }
            }, "")
        }

        @Contribute(SymbolProvider.class)
        @ApplicationDefaults
        public static void setupApplicationDefaults(MappedConfiguration<String, Object> configuration) {
            configuration.add(MongoDBSymbols.DEFAULT_DB_NAME, "test")
        }

        @Contribute(JongoCollectionPackageManager.class)
        public static void provideCollectionPackages(Configuration<String> configuration) {
            configuration.add("demo.model")
        }



        @Contribute(ServiceOverride.class)
        public static void overrideMongoDBSource(MappedConfiguration<Class, Object> configuration,
                                                 @Local MongoDBSource override) {
            configuration.add(MongoDBSource.class, override)
        }
    }

}
