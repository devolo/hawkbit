/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui;

import org.eclipse.hawkbit.im.authentication.PermissionService;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.vaadin.spring.servlet.Vaadin4SpringServlet;

import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinServlet;

/**
 * Enables UI components for the Management UI.
 *
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(UiProperties.class)
@PropertySource("classpath:/hawkbit-ui-defaults.properties")
public class MgmtUiConfiguration {

    /**
     * Permission checker for UI.
     * 
     * @param permissionService
     *            PermissionService
     *
     * @return Permission checker for UI
     */
    @Bean
    @ConditionalOnMissingBean
    SpPermissionChecker spPermissionChecker(final PermissionService permissionService) {
        return new SpPermissionChecker(permissionService);
    }

    /**
     * Utility for Vaadin messages source.
     * 
     * @param source
     *            Delegate MessageSource
     *
     * @return Vaadin messages source utility
     */
    @Bean
    @ConditionalOnMissingBean
    VaadinMessageSource messageSourceVaadin(final MessageSource source) {
        return new VaadinMessageSource(source);
    }

    /**
     * Localized system message provider bean.
     * 
     * @param uiProperties
     *            UiProperties
     * @param i18n
     *            VaadinMessageSource
     *
     * @return Localized system message provider
     */
    @Bean
    @ConditionalOnMissingBean
    SystemMessagesProvider systemMessagesProvider(final UiProperties uiProperties, final VaadinMessageSource i18n) {
        return new LocalizedSystemMessagesProvider(uiProperties, i18n);
    }

    /**
     * Vaadin4Spring servlet bean.
     *
     * @return Vaadin servlet for Spring
     */
    @Bean
    public VaadinServlet vaadinServlet() {
        return new Vaadin4SpringServlet();
    }
}
