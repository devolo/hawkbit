/**
 * Copyright (c) 2021 Bosch.IO GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.repository;

/**
 * Describing the fields of the TargetType model which can be used in
 * the REST API
 */
public enum TargetTypeFields implements FieldNameProvider {
    /**
     * The name field.
     */
    NAME("name"),
    /**
     * The description field.
     */
    DESCRIPTION("description"),

    /**
     * The id field.
     */
    ID("id");

    private final String fieldName;

    TargetTypeFields(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
