/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common.filterlayout;

import java.util.Collection;

import org.eclipse.hawkbit.ui.SpPermissionChecker;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyIdentifiableEntity;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyType;
import org.eclipse.hawkbit.ui.common.event.EntityModifiedEventPayload;
import org.eclipse.hawkbit.ui.common.event.EntityModifiedEventPayload.EntityModifiedEventType;
import org.eclipse.hawkbit.ui.common.event.EventTopics;
import org.eclipse.hawkbit.ui.common.event.EventView;
import org.eclipse.hawkbit.ui.common.event.FilterChangedEventPayload;
import org.eclipse.hawkbit.ui.common.event.FilterType;
import org.eclipse.hawkbit.ui.common.filterlayout.AbstractFilterButtonClickBehaviour.ClickBehaviourType;
import org.eclipse.hawkbit.ui.common.state.TypeFilterLayoutUiState;
import org.eclipse.hawkbit.ui.utils.UINotification;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;
import org.vaadin.spring.events.EventBus.UIEventBus;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * Class for defining the type filter buttons.
 */
public abstract class AbstractTypeFilterButtons extends AbstractFilterButtons<ProxyType, String> {
    private static final long serialVersionUID = 1L;

    private final TypeFilterLayoutUiState typeFilterLayoutUiState;

    private final UINotification uiNotification;
    private final transient TypeFilterButtonClick typeFilterButtonClick;

    /**
     * Constructor for AbstractTypeFilterButtons
     *
     * @param eventBus
     *            UIEventBus
     * @param i18n
     *            VaadinMessageSource
     * @param uiNotification
     *            UINotification
     * @param permChecker
     *            SpPermissionChecker
     * @param typeFilterLayoutUiState
     *            TypeFilterLayoutUiState
     */
    public AbstractTypeFilterButtons(final UIEventBus eventBus, final VaadinMessageSource i18n,
            final UINotification uiNotification, final SpPermissionChecker permChecker,
            final TypeFilterLayoutUiState typeFilterLayoutUiState) {
        super(eventBus, i18n, uiNotification, permChecker);

        this.uiNotification = uiNotification;
        this.typeFilterLayoutUiState = typeFilterLayoutUiState;
        this.typeFilterButtonClick = new TypeFilterButtonClick(this::onFilterChanged);
    }

    @Override
    protected TypeFilterButtonClick getFilterButtonClickBehaviour() {
        return typeFilterButtonClick;
    }

    private void onFilterChanged(final ProxyType typeFilter, final ClickBehaviourType clickType) {
        getDataCommunicator().reset();

        final Long typeId = ClickBehaviourType.CLICKED == clickType ? typeFilter.getId() : null;

        publishFilterChangedEvent(typeId);
    }

    private void publishFilterChangedEvent(final Long typeId) {
        eventBus.publish(EventTopics.FILTER_CHANGED, this,
                new FilterChangedEventPayload<>(getFilterMasterEntityType(), FilterType.TYPE, typeId, getView()));

        typeFilterLayoutUiState.setClickedTypeId(typeId);
    }

    protected abstract Class<? extends ProxyIdentifiableEntity> getFilterMasterEntityType();

    protected abstract EventView getView();

    @Override
    protected boolean deleteFilterButtons(final Collection<ProxyType> filterButtonsToDelete) {
        // We do not allow multiple deletion yet
        final ProxyType typeToDelete = filterButtonsToDelete.iterator().next();
        final String typeToDeleteName = typeToDelete.getName();
        final Long typeToDeleteId = typeToDelete.getId();

        final Long clickedTypeId = getFilterButtonClickBehaviour().getPreviouslyClickedFilterId();

        if (clickedTypeId != null && clickedTypeId.equals(typeToDeleteId)) {
            uiNotification.displayValidationError(i18n.getMessage("message.type.delete", typeToDeleteName));

            return false;
        } else if (isDefaultType(typeToDelete)) {
            uiNotification.displayValidationError(i18n.getMessage("message.cannot.delete.default.dstype"));

            return false;
        } else {
            deleteType(typeToDelete);

            eventBus.publish(EventTopics.ENTITY_MODIFIED, this,
                    new EntityModifiedEventPayload(EntityModifiedEventType.ENTITY_REMOVED, getFilterMasterEntityType(),
                            ProxyType.class, typeToDeleteId));

            return true;
        }
    }

    protected abstract boolean isDefaultType(final ProxyType typeToDelete);

    protected abstract void deleteType(final ProxyType typeToDelete);

    /**
     * Reset the filter after types deleted
     *
     * @param deletedTypeIds
     *            List of deleted type Ids
     */
    public void resetFilterOnTypesDeleted(final Collection<Long> deletedTypeIds) {
        final Long clickedTypeId = getFilterButtonClickBehaviour().getPreviouslyClickedFilterId();

        if (clickedTypeId != null && deletedTypeIds.contains(clickedTypeId)) {
            getFilterButtonClickBehaviour().setPreviouslyClickedFilterId(null);
            publishFilterChangedEvent(null);
        }
    }

    @Override
    protected boolean isDeletionAllowed() {
        return permissionChecker.hasDeleteRepositoryPermission();
    }

    @Override
    protected boolean isEditAllowed() {
        return permissionChecker.hasUpdateRepositoryPermission();
    }

    @Override
    protected void editButtonClickListener(final ProxyType clickedFilter) {
        final Window updateWindow = getUpdateWindow(clickedFilter);

        updateWindow.setCaption(i18n.getMessage("caption.update", i18n.getMessage("caption.type")));
        UI.getCurrent().addWindow(updateWindow);
        updateWindow.setVisible(Boolean.TRUE);
    }

    protected abstract Window getUpdateWindow(final ProxyType clickedFilter);

    @Override
    public void restoreState() {
        final Long lastClickedTypeId = typeFilterLayoutUiState.getClickedTypeId();

        if (lastClickedTypeId != null) {
            getFilterButtonClickBehaviour().setPreviouslyClickedFilterId(lastClickedTypeId);
        }
    }
}
