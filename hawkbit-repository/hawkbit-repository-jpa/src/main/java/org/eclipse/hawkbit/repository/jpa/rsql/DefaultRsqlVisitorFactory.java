/**
 * Copyright (c) 2021 Bosch.IO GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.repository.jpa.rsql;

import org.eclipse.hawkbit.repository.FieldNameProvider;
import org.eclipse.hawkbit.repository.rsql.RsqlVisitorFactory;

import cz.jirutka.rsql.parser.ast.RSQLVisitor;

/**
 * Factory providing {@link RSQLVisitor} instances which validate the nodes
 * based on a given {@link FieldNameProvider}.
 */
public class DefaultRsqlVisitorFactory implements RsqlVisitorFactory {

    @Override
    public <A extends Enum<A> & FieldNameProvider> RSQLVisitor<Void, String> validationRsqlVisitor(
            final Class<A> fieldNameProvider) {
        return new FieldValidationRsqlVisitor<>(fieldNameProvider);
    }

}
