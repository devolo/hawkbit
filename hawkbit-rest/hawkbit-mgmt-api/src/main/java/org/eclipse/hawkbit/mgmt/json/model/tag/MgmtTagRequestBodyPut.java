/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.mgmt.json.model.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request Body for PUT/POST.
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MgmtTagRequestBodyPut {

    @JsonProperty
    @Schema(example = "rgb(0,255,0)")
    private String colour;

    @JsonProperty
    @Schema(example = "Example name")
    private String name;

    @JsonProperty
    @Schema(example = "Example description")
    private String description;

    public String getName() {
        return name;
    }

    public MgmtTagRequestBodyPut setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MgmtTagRequestBodyPut setDescription(final String description) {
        this.description = description;
        return this;
    }

    public MgmtTagRequestBodyPut setColour(final String colour) {
        this.colour = colour;
        return this;
    }

    public String getColour() {
        return colour;
    }
}
