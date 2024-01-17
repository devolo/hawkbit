/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.repository.event.remote;

import org.eclipse.hawkbit.repository.model.TenantAwareBaseEntity;

import java.util.Arrays;

/**
 * An base definition class for an event which contains an id.
 *
 */
public class RemoteIdEvent extends RemoteTenantAwareEvent {

    private static final long serialVersionUID = 1L;

    private Long entityId;

    private String entityClass;

    private String interfaceClass;

    /**
     * Default constructor.
     */
    protected RemoteIdEvent() {
        // for serialization libs like jackson
    }

    /**
     * Constructor for json serialization.
     * 
     * @param entityId
     *            the entity Id
     * @param tenant
     *            the tenant
     * @param entityClass
     *            the entity class
     * @param applicationId
     *            the origin application id
     */
    protected RemoteIdEvent(final Long entityId, final String tenant,
            final Class<? extends TenantAwareBaseEntity> entityClass, final String applicationId) {
        super(entityId, tenant, applicationId);
        this.entityClass = entityClass.getName();
        this.interfaceClass = entityClass.isInterface() ? entityClass.getName()
                : getInterfaceEntity(entityClass).getName();
        this.entityId = entityId;
    }

    private static Class<?> getInterfaceEntity(final Class<? extends TenantAwareBaseEntity> baseEntity) {
        final Class<?>[] interfaces = baseEntity.getInterfaces();
        return Arrays.stream(interfaces).filter(TenantAwareBaseEntity.class::isAssignableFrom).findFirst()
                .orElse(baseEntity);
    }

    /**
     * @return the entityClass
     */
    public String getEntityClass() {
        return entityClass;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getInterfaceClass() {
        return interfaceClass;
    }
}
