/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.repository;

import org.eclipse.hawkbit.repository.model.Action;
import org.eclipse.hawkbit.repository.model.ActionStatus;
import org.eclipse.hawkbit.repository.model.DistributionSetType;
import org.eclipse.hawkbit.repository.model.SoftwareModuleMetadata;

/**
 * Repository constants.
 *
 */
public final class RepositoryConstants {

    /**
     * Prefix that the server puts in front of
     * {@link ActionStatus#getMessages()} if the message is generated by the
     * server.
     */
    public static final String SERVER_MESSAGE_PREFIX = "Update Server: ";

    /**
     * Number of {@link DistributionSetType}s that are generated as part of
     * default tenant setup.
     */
    public static final int DEFAULT_DS_TYPES_IN_TENANT = 3;

    /**
     * Maximum number of actions that can be retrieved.
     */
    public static final int MAX_ACTION_COUNT = 100;

    /**
     * Maximum number of messages that can be retrieved by a controller for an
     * {@link Action}.
     */
    public static final int MAX_ACTION_HISTORY_MSG_COUNT = 100;

    /**
     * Maximum number of metadata entries provided to controllers.
     * 
     * @see SoftwareModuleMetadata#isTargetVisible()
     */
    public static final int MAX_META_DATA_COUNT = 50;

    private RepositoryConstants() {
        // Utility class.
    }

}
