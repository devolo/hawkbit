/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.ui.decorators;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Style for button: Tag.
 */
public class SPUITagButtonStyle implements SPUIButtonDecorator {

    @Override
    public Button decorate(final Button button, final String style, final boolean setStyle, final Resource icon) {

        button.addStyleName("button-no-border");
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        button.addStyleName("button-tag-no-border");
        button.addStyleName("text-cut");

        // Set Style
        if (null != style) {
            if (setStyle) {
                button.setStyleName(style);
            } else {
                button.addStyleName(style);
            }
        }
        // Set icon
        if (null != icon) {
            button.setIcon(icon);
        }
        return button;
    }
}
