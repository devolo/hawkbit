/**
 * Copyright (c) 2020 Bosch.IO GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common.grid.header;

import java.util.Arrays;

import org.eclipse.hawkbit.ui.SpPermissionChecker;
import org.eclipse.hawkbit.ui.common.detailslayout.EditDetailsHeaderSupport;
import org.eclipse.hawkbit.ui.common.detailslayout.MetaDataDetailsHeaderSupport;
import org.eclipse.hawkbit.ui.utils.UINotification;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;
import org.vaadin.spring.events.EventBus.UIEventBus;

/**
 * Header for entity details with edit and metadata support.
 *
 * @param <T>
 *          Generic type
 */
public abstract class AbstractDetailsHeader<T> extends AbstractMasterAwareGridHeader<T> {
    private static final long serialVersionUID = 1L;

    protected final UINotification uiNotification;

    private final transient EditDetailsHeaderSupport editDetailsHeaderSupport;
    private final transient MetaDataDetailsHeaderSupport metaDataDetailsHeaderSupport;

    protected transient T selectedEntity;

    /**
     * Constructor for AbstractDetailsHeader
     *
     * @param i18n
     *          VaadinMessageSource
     * @param permChecker
     *          SpPermissionChecker
     * @param eventBus
     *          UIEventBus
     * @param uiNotification
     *          UINotification
     */
    public AbstractDetailsHeader(final VaadinMessageSource i18n, final SpPermissionChecker permChecker,
            final UIEventBus eventBus, final UINotification uiNotification) {
        super(i18n, permChecker, eventBus);

        this.uiNotification = uiNotification;

        if (hasEditPermission()) {
            this.editDetailsHeaderSupport = new EditDetailsHeaderSupport(i18n, getEditIconId(), this::onEdit);
            this.metaDataDetailsHeaderSupport = new MetaDataDetailsHeaderSupport(i18n, getMetaDataIconId(),
                    this::showMetaData);
        } else {
            this.editDetailsHeaderSupport = null;
            this.metaDataDetailsHeaderSupport = null;
        }

        addHeaderSupports(Arrays.asList(editDetailsHeaderSupport, metaDataDetailsHeaderSupport));
    }

    protected abstract boolean hasEditPermission();

    protected abstract String getEditIconId();

    protected abstract void onEdit();

    protected abstract String getMetaDataIconId();

    protected abstract void showMetaData();

    @Override
    protected void init() {
        setSpacing(false);
        setMargin(false);
    }

    @Override
    protected String getEntityDetailsCaptionMsgKey() {
        return getMasterEntityType();
    }

    protected abstract String getMasterEntityType();

    @Override
    protected String getEntityDetailsCaptionOfMsgKey() {
        return getMasterEntityType() + ":";
    }

    @Override
    public void masterEntityChanged(final T entity) {
        super.masterEntityChanged(entity);

        if (entity == null) {
            disableEdit();
            disableMetaData();
        } else {
            enableEdit();
            enableMetaData();
        }

        selectedEntity = entity;
    }

    private void disableEdit() {
        if (editDetailsHeaderSupport != null) {
            editDetailsHeaderSupport.disableEditIcon();
        }
    }

    private void disableMetaData() {
        if (metaDataDetailsHeaderSupport != null) {
            metaDataDetailsHeaderSupport.disableMetaDataIcon();
        }
    }

    private void enableEdit() {
        if (editDetailsHeaderSupport != null) {
            editDetailsHeaderSupport.enableEditIcon();
        }
    }

    private void enableMetaData() {
        if (metaDataDetailsHeaderSupport != null) {
            metaDataDetailsHeaderSupport.enableMetaDataIcon();
        }
    }
}
