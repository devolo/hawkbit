/**
 * Copyright (c) 2020 Microsoft and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.repository.test.util;

import java.util.Map;

import org.junit.jupiter.api.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link Extension} for creating and dropping MySql schemas if
 * tests are set up with MySql.
 */
public class PostgreSqlTestDatabase extends AbstractSqlTestDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlTestDatabase.class);
    private static final String POSTGRESQL_URI_PATTERN = "jdbc:postgresql://{host}:{port}/{db}*";

    public PostgreSqlTestDatabase(final DatasourceContext context) {
        super(context);
    }

    @Override
    protected PostgreSqlTestDatabase createRandomSchema() {
        LOGGER.info("\033[0;33mCreating postgreSql schema {} \033[0m", context.getRandomSchemaName());
        final String uri = getBaseUri() + "?currentSchema=" + getSchemaName();
        executeStatement(uri, "CREATE SCHEMA " + context.getRandomSchemaName() + ";");
        return this;
    }

    @Override
    protected void dropRandomSchema() {
        LOGGER.info("\033[0;33mDropping postgreSql schema {}\033[0m", context.getRandomSchemaName());
        final String uri = getBaseUri() + "?currentSchema=" + getSchemaName();
        executeStatement(uri, "DROP SCHEMA " + context.getRandomSchemaName() + " CASCADE;");
    }

    @Override
    protected String getRandomSchemaUri() {
        return getBaseUri() + "?currentSchema=" + context.getRandomSchemaName();
    }

    private String getBaseUri() {
        final String uri = context.getDatasourceUrl();
        final Map<String, String> databaseProperties = MATCHER.extractUriTemplateVariables(POSTGRESQL_URI_PATTERN, uri);

        return POSTGRESQL_URI_PATTERN.replace("{host}", databaseProperties.get("host"))
                .replace("{port}", databaseProperties.get("port"))
                .replace("{db}*", getSchemaName());
    }

    private String getSchemaName() {
        return MATCHER.extractUriTemplateVariables(POSTGRESQL_URI_PATTERN, context.getDatasourceUrl())
                .get("db")
                .split("\\?")[0];
    }
}
