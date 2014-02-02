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

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.apache.tapestry5.mongodb.MongoDBSource;
import org.jongo.util.RandomPortNumberGenerator;

/**
* Embedded MongoDB Source implementation
 */
public class EmbeddedMongoDBSource implements MongoDBSource {

    public EmbeddedMongoDBSource() {
    }

    @Override
    public MongoClient getMongo() {
        try {

            int port = RandomPortNumberGenerator.pickAvailableRandomEphemeralPortNumber();

            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(port, Network.localhostIsIPv6()))
                    .build();

            MongodStarter runtime = MongodStarter.getDefaultInstance();

            MongodExecutable mongodExecutable = null;

            mongodExecutable = runtime.prepare(mongodConfig);
            MongodProcess mongod = mongodExecutable.start();

            MongoClient mongo = new MongoClient("localhost", port);

            return mongo;

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Embedded Mongo instance: " + e, e);
        }

    }
}
