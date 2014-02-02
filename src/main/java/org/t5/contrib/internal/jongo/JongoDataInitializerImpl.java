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

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.t5.contrib.jongo.JongoDataInitializer;
import org.t5.contrib.jongo.JongoService;

/**
 * Default implementation for {@link org.t5.contrib.jongo.JongoDataInitializer}
 */
public class JongoDataInitializerImpl implements JongoDataInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JongoDataInitializer.class);

    private final boolean productionMode;
    private final JongoService jongoService;

    public JongoDataInitializerImpl(JongoService jongoService,
                                    @Inject @Symbol(SymbolConstants.PRODUCTION_MODE)
                                    boolean productionMode) {

        this.jongoService = jongoService;
        this.productionMode = productionMode;
    }


    public void init() {

        // Skip initialization when in Production mode
        if (productionMode) {
            return;
        }

        LOGGER.info("Initializing MongoDB Database");

        populate(jongoService);

    }

    protected void populate(JongoService jongoService) {

    }

}