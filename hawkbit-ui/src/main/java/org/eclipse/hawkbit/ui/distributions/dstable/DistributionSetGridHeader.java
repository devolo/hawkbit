/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.distributions.dstable;

import org.eclipse.hawkbit.ui.SpPermissionChecker;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyDistributionSet;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyIdentifiableEntity;
import org.eclipse.hawkbit.ui.common.event.EventLayout;
import org.eclipse.hawkbit.ui.common.event.EventView;
import org.eclipse.hawkbit.ui.common.grid.header.AbstractEntityGridHeader;
import org.eclipse.hawkbit.ui.common.state.GridLayoutUiState;
import org.eclipse.hawkbit.ui.common.state.HidableLayoutUiState;
import org.eclipse.hawkbit.ui.utils.UIComponentIdProvider;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;
import org.vaadin.spring.events.EventBus.UIEventBus;

/**
 * Distribution table header.
 */
public class DistributionSetGridHeader extends AbstractEntityGridHeader {
    private static final long serialVersionUID = 1L;

    private static final String DS_TABLE_HEADER = "header.dist.table";
    private static final String DS_CAPTION = "caption.distribution";

    /**
     * Constructor for DistributionSetGridHeader
     *
     * @param i18n
     *          VaadinMessageSource
     * @param permChecker
     *          SpPermissionChecker
     * @param eventBus
     *          UIEventBus
     * @param dSTypeFilterLayoutUiState
     *          HidableLayoutUiState
     * @param distributionSetGridLayoutUiState
     *          GridLayoutUiState
     * @param filterLayout
     *          EventLayout
     * @param view
     *          EventView
     */
    public DistributionSetGridHeader(final VaadinMessageSource i18n, final SpPermissionChecker permChecker,
            final UIEventBus eventBus, final HidableLayoutUiState dSTypeFilterLayoutUiState,
            final GridLayoutUiState distributionSetGridLayoutUiState, final EventLayout filterLayout,
            final EventView view) {
        super(i18n, permChecker, eventBus, dSTypeFilterLayoutUiState, distributionSetGridLayoutUiState, filterLayout,
                view);
    }

    @Override
    protected String getCaptionMsg() {
        return DS_TABLE_HEADER;
    }

    @Override
    protected String getSearchFieldId() {
        return UIComponentIdProvider.DIST_SEARCH_TEXTFIELD;
    }

    @Override
    protected String getSearchResetIconId() {
        return UIComponentIdProvider.DIST_SEARCH_ICON;
    }

    @Override
    protected Class<? extends ProxyIdentifiableEntity> getEntityType() {
        return ProxyDistributionSet.class;
    }

    @Override
    protected String getFilterButtonsIconId() {
        return UIComponentIdProvider.SHOW_DIST_TAG_ICON;
    }

    @Override
    protected String getMaxMinIconId() {
        return UIComponentIdProvider.DS_MAX_MIN_TABLE_ICON;
    }

    @Override
    protected EventLayout getLayout() {
        return EventLayout.DS_LIST;
    }

    @Override
    protected boolean hasCreatePermission() {
        return permChecker.hasCreateRepositoryPermission();
    }

    @Override
    protected String getAddIconId() {
        return UIComponentIdProvider.DIST_ADD_ICON;
    }

    @Override
    protected String getAddWindowCaptionMsg() {
        return DS_CAPTION;
    }
}
