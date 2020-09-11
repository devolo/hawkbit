/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.components;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.eclipse.hawkbit.ui.common.notification.ParallelNotification;
import org.eclipse.hawkbit.ui.utils.SPUIStyleDefinitions;
import org.eclipse.hawkbit.ui.utils.SpringContextHelper;
import org.eclipse.hawkbit.ui.utils.UINotification;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClientConnector.ConnectorErrorEvent;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.Page;
import com.vaadin.server.UploadException;
import com.vaadin.shared.Connector;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * Default handler for Hawkbit UI.
 */
public class HawkbitUIErrorHandler extends DefaultErrorHandler {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(HawkbitUIErrorHandler.class);

    @Override
    public void error(final ErrorEvent event) {

        // filter upload exceptions
        if (event.getThrowable() instanceof UploadException) {
            return;
        }

        final Notification notification = buildNotification(getRootExceptionFrom(event));
        if (event instanceof ConnectorErrorEvent) {
            final Connector connector = ((ConnectorErrorEvent) event).getConnector();
            if (connector instanceof UI) {
                final UI uiInstance = (UI) connector;
                uiInstance.access(() -> notification.show(uiInstance.getPage()));
                return;
            }
        }

        final Optional<Page> originError = getPageOriginError(event);
        if (originError.isPresent()) {
            notification.show(originError.get());
            return;
        }

        notification.show(Page.getCurrent());
    }

    private static Throwable getRootExceptionFrom(final ErrorEvent event) {
        return getRootCauseOf(event.getThrowable());
    }

    private static Throwable getRootCauseOf(final Throwable ex) {

        if (ex.getCause() != null) {
            return getRootCauseOf(ex.getCause());
        }

        return ex;
    }

    private static Optional<Page> getPageOriginError(final ErrorEvent event) {

        final Component errorOrigin = findAbstractComponent(event);

        if (errorOrigin != null && errorOrigin.getUI() != null) {
            return Optional.ofNullable(errorOrigin.getUI().getPage());
        }

        return Optional.empty();
    }

    /**
     * Method to build a notification based on an exception.
     * 
     * @param ex
     *            the throwable
     * @return a hawkbit error notification message
     */
    protected ParallelNotification buildNotification(final Throwable ex) {

        LOG.error("Error in UI: ", ex);

        final String errorMessage = extractMessageFrom(ex);
        final VaadinMessageSource i18n = SpringContextHelper.getBean(VaadinMessageSource.class);

        return buildErrorNotification(i18n.getMessage("caption.error"), errorMessage);
    }

    /**
     * Method to build a error notification based on caption and description.
     * 
     * @param caption
     *            Caption
     * @param description
     *            Description
     * @return a hawkbit error notification message
     */
    protected static ParallelNotification buildErrorNotification(final String caption, final String description) {
        return UINotification.buildNotification(SPUIStyleDefinitions.SP_NOTIFICATION_ERROR_MESSAGE_STYLE, caption,
                description, VaadinIcons.EXCLAMATION_CIRCLE, true);
    }

    private static String extractMessageFrom(final Throwable ex) {

        if (!(ex instanceof ConstraintViolationException)) {
            if (!StringUtils.isEmpty(ex.getMessage())) {
                return ex.getMessage();
            }
            return ex.getClass().getSimpleName();
        }

        final Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) ex).getConstraintViolations();

        if (violations == null) {
            return ex.getClass().getSimpleName();
        } else {
            return violations.stream().map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
