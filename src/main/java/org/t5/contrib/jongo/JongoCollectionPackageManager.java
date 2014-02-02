package org.t5.contrib.jongo;

import java.util.Collection;

/**
 * Contains a set of contributed package names from which to load collections
 * </p>
 * The service's configuration is the names of Java packages to search for Mongo collections.
 */
public interface JongoCollectionPackageManager {
    /**
     * Returns packages from which read to collection classes
     */
    Collection<String> getPackageNames();
}
