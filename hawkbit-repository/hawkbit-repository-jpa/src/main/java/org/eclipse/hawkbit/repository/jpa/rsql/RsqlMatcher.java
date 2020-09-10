/**
 * Copyright (c) 2020 devolo AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.eclipse.hawkbit.repository.jpa.TargetFieldData;

public interface RsqlMatcher {
    static boolean matches(String rsql, TargetFieldData fieldData){

        Node rootNode = new RSQLParser().parse(rsql.toLowerCase());

        RSQLVisitor<Boolean, TargetFieldData> visitor = new RsqlVisitor();

        return rootNode.accept(visitor, fieldData);
    }
}
