/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * An interface for declaring the name of the field described in the database
 * which is used as string representation of the field, e.g. for sorting the
 * fields over REST.
 */
@FunctionalInterface
public interface FieldNameProvider {

    /**
     * Separator for the sub attributes
     */
    String SUB_ATTRIBUTE_SEPERATOR = ".";

    /**
     * @return the string representation of the underlying persistence field
     *         name e.g. in case of sorting. Never {@code null}.
     */
    String getFieldName();

    /**
     * Contains the sub entity the given field.
     *
     * @param propertyField
     *            the given field
     * @return <code>true</code> contains <code>false</code> contains not
     */
    default boolean containsSubEntityAttribute(final String propertyField) {

        final List<String> subEntityAttributes = getSubEntityAttributes();
        if (subEntityAttributes.contains(propertyField)) {
            return true;
        }
        for (final String attribute : subEntityAttributes) {
            final String[] graph = attribute.split("\\" + SUB_ATTRIBUTE_SEPERATOR);

            for (final String subAttribute : graph) {
                if (subAttribute.equalsIgnoreCase(propertyField)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return all sub entities attributes.
     */
    default List<String> getSubEntityAttributes() {
        return Collections.emptyList();
    }

    /**
     * @return a key/value tuple of a sub entity.
     */
    default Optional<Entry<String, String>> getSubEntityMapTuple() {
        return Optional.empty();
    }

    /**
     * Is the entity field a {@link Map}.
     *
     * @return <code>true</code> is a map <code>false</code> is not a map
     */
    default boolean isMap() {
        return false;
    }

    /**
     * Returns the name of the field, that identifies the entity.
     * 
     * @return the name of the identifier, by default 'id'
     */
    default String identifierFieldName() {
        return "id";
    }
}
