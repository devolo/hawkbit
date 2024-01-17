/**
 * Copyright (c) 2018 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.repository.event.remote.entity;

import org.eclipse.hawkbit.repository.event.entity.EntityUpdatedEvent;
import org.eclipse.hawkbit.repository.model.TargetFilterQuery;

/**
 * Defines the remote event for updating a {@link TargetFilterQuery}.
 *
 */
public class TargetFilterQueryUpdatedEvent extends RemoteEntityEvent<TargetFilterQuery> implements EntityUpdatedEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public TargetFilterQueryUpdatedEvent() {
        // for serialization libs like jackson
    }

    /**
     * Constructor.
     * 
     * @param baseEntity
     *            TargetFilterQuery entity
     * @param applicationId
     *            the origin application id
     */
    public TargetFilterQueryUpdatedEvent(final TargetFilterQuery baseEntity, final String applicationId) {
        super(baseEntity, applicationId);
    }

}
