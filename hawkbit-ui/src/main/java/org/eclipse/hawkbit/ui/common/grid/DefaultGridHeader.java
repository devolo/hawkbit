/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common.grid;

import org.eclipse.hawkbit.ui.components.SPUIButton;
import org.eclipse.hawkbit.ui.components.SPUIComponentProvider;
import org.eclipse.hawkbit.ui.decorators.SPUIButtonStyleNoBorder;
import org.eclipse.hawkbit.ui.management.state.ManagementUIState;
import org.eclipse.hawkbit.ui.utils.SPUIStyleDefinitions;
import org.eclipse.hawkbit.ui.utils.UIMessageIdProvider;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;
import org.springframework.util.StringUtils;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Abstract grid header placed on top of a grid.
 */
public class DefaultGridHeader extends VerticalLayout {
    private static final long serialVersionUID = 1921798400953670917L;

    private final ManagementUIState managementUIState;

    private final String titleText;
    private Label title;
    private Label prefixText;
    private HorizontalLayout prefixWithTitle;
    private HorizontalLayout titleLayout;
    private transient AbstractHeaderMaximizeSupport maximizeSupport;

    private final VaadinMessageSource i18n;

    /**
     * Constructor.
     *
     * @param managementUIState
     */
    public DefaultGridHeader(final ManagementUIState managementUIState, final VaadinMessageSource i18n) {
        this(managementUIState, "", i18n);
    }

    /**
     * Constructor.
     *
     * @param managementUIState
     * @param titleText
     */
    public DefaultGridHeader(final ManagementUIState managementUIState, final String titleText,
            final VaadinMessageSource i18n) {
        this.managementUIState = managementUIState;
        this.titleText = titleText;
        this.i18n = i18n;
    }

    /**
     * Initializes the header.
     *
     * @return this DefaultGridHeader in order to allow method chaining
     */
    public DefaultGridHeader init() {
        buildTitleHorizontalLayout();
        buildTitleLayout();
        buildComponent();
        return this;
    }

    /**
     * Builds the title HorizontalLayout containing two labels.
     *
     * @return title as HorizontalLayout
     */
    protected HorizontalLayout buildTitleHorizontalLayout() {

        prefixText = new Label();
        prefixText.setValue(titleText);
        prefixText.addStyleName(ValoTheme.LABEL_SMALL);
        prefixText.addStyleName(ValoTheme.LABEL_BOLD);
        prefixText.setSizeUndefined();

        title = new Label();
        title.setSizeFull();
        title.setImmediate(true);
        title.setWidth("100%");
        title.addStyleName(ValoTheme.LABEL_SMALL);
        title.addStyleName("text-bold");
        title.addStyleName("text-cut");
        title.addStyleName("header-caption-right");

        prefixWithTitle = new HorizontalLayout();
        prefixWithTitle.setMargin(false);
        prefixWithTitle.setSpacing(true);
        prefixWithTitle.setSizeFull();
        prefixWithTitle.addStyleName("header-caption");

        prefixWithTitle.addComponent(prefixText);
        prefixWithTitle.setComponentAlignment(prefixText, Alignment.TOP_LEFT);
        prefixWithTitle.setExpandRatio(prefixText, 0.0F);

        prefixWithTitle.addComponent(title);
        prefixWithTitle.setComponentAlignment(title, Alignment.TOP_LEFT);
        prefixWithTitle.setExpandRatio(title, 1.0F);

        return prefixWithTitle;
    }

    /**
     * Builds the title layout.
     *
     * @return title-layout
     */
    protected HorizontalLayout buildTitleLayout() {
        titleLayout = new HorizontalLayout();
        titleLayout.addStyleName(SPUIStyleDefinitions.WIDGET_TITLE);
        titleLayout.setSpacing(false);
        titleLayout.setMargin(false);
        titleLayout.setSizeFull();
        titleLayout.addComponent(prefixWithTitle);
        titleLayout.setComponentAlignment(prefixWithTitle, Alignment.TOP_LEFT);
        titleLayout.setExpandRatio(prefixWithTitle, 0.8F);

        if (hasHeaderMaximizeSupport()) {
            titleLayout.addComponents(getHeaderMaximizeSupport().maxMinButton);
            titleLayout.setComponentAlignment(getHeaderMaximizeSupport().maxMinButton, Alignment.TOP_RIGHT);
            titleLayout.setExpandRatio(getHeaderMaximizeSupport().maxMinButton, 0.2F);
        }

        return titleLayout;
    }

    /**
     * Builds the layout for the header component.
     */
    protected void buildComponent() {
        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_LEFT);
        setWidth(100, Unit.PERCENTAGE);
        setImmediate(true);
        addStyleName("action-history-header");
        addStyleName("bordered-layout");
        addStyleName("no-border-bottom");
    }

    /**
     * Enables maximize-support for the header by setting a HeaderMaximizeSupport
     * implementation.
     *
     * @param maximizeSupport
     *            encapsulates layout of min-max-button and behavior for minimize
     *            and maximize.
     */
    public void setHeaderMaximizeSupport(final AbstractHeaderMaximizeSupport maximizeSupport) {
        this.maximizeSupport = maximizeSupport;
    }

    /**
     * Gets the HeaderMaximizeSupport implementation describing behavior for
     * minimize and maximize.
     *
     * @return maximizeSupport that encapsulates behavior for minimize and maximize.
     */
    public AbstractHeaderMaximizeSupport getHeaderMaximizeSupport() {
        return maximizeSupport;
    }

    /**
     * Checks whether maximize-support is enabled.
     *
     * @return <code>true</code> if maximize-support is enabled, otherwise
     *         <code>false</code>
     */
    public boolean hasHeaderMaximizeSupport() {
        return maximizeSupport != null;
    }

    /**
     * Updates the title of the header.
     *
     * @param newTitle
     */
    public void updateTitle(final String newTitle, final String hasTextCasePrefixText, final String hasNoTextCasePrefixText) {

        if (StringUtils.hasText(newTitle)) {
            prefixText.setValue(i18n.getMessage(hasTextCasePrefixText));
            title.setValue(newTitle);
        } else {
            prefixText.setValue(i18n.getMessage(hasNoTextCasePrefixText));
            title.setValue("");
        }
    }

    /**
     * The Implemented capability offers a button that triggers minimization and
     * maximization.
     */
    public abstract class AbstractHeaderMaximizeSupport {

        private final SPUIButton maxMinButton;

        /**
         * Constructor.
         *
         * @param maximizeButtonId
         */
        protected AbstractHeaderMaximizeSupport(final String maximizeButtonId) {
            maxMinButton = createMinMaxButton(maximizeButtonId);
            // listener for maximizing action history
            maxMinButton.addClickListener(event -> maxMinButtonClicked());
        }

        /**
         * Invoked when min-max-button is pressed.
         */
        private void maxMinButtonClicked() {
            final Boolean flag = (Boolean) maxMinButton.getData();
            if (flag == null || Boolean.FALSE.equals(flag)) {
                // Clicked on max Icon
                showMinIcon();
                maximize();
                managementUIState.setActionHistoryMaximized(true);
            } else {
                // Clicked on min icon
                showMaxIcon();
                minimize();
                managementUIState.setActionHistoryMaximized(false);
            }
        }

        /**
         * Additional actions for maximize operation might be performed by this method.
         */
        protected abstract void maximize();

        /**
         * Additional actions for minimize operation might be performed by this method.
         */
        protected abstract void minimize();

        /**
         * Creates a min-max-button instance.
         *
         * @param buttonId
         *            the button id for the min-max-button
         * @return newly cretaed min-max-button
         */
        protected SPUIButton createMinMaxButton(final String buttonId) {
            return (SPUIButton) SPUIComponentProvider.getButton(buttonId, "",
                    i18n.getMessage(UIMessageIdProvider.TOOLTIP_MAXIMIZE), null, true, FontAwesome.EXPAND,
                    SPUIButtonStyleNoBorder.class);
        }

        /**
         * Styles min-max-button icon with minimize decoration
         */
        public void showMinIcon() {
            maxMinButton.toggleIcon(FontAwesome.COMPRESS);
            maxMinButton.setDescription(i18n.getMessage(UIMessageIdProvider.TOOLTIP_MINIMIZE));
            maxMinButton.setData(Boolean.TRUE);
        }

        /**
         * Styles min-max-button icon with maximize decoration
         */
        public void showMaxIcon() {
            maxMinButton.toggleIcon(FontAwesome.EXPAND);
            maxMinButton.setDescription(i18n.getMessage(UIMessageIdProvider.TOOLTIP_MAXIMIZE));
            maxMinButton.setData(Boolean.FALSE);
        }
    }
}
