/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.ddi.json.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Additional metadata to be provided for the target/device.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DdiMetadata {
    @JsonProperty
    @NotNull
    @Schema(example = "aMetadataKey")
    private final String key;

    @JsonProperty
    @NotNull
    @Schema(example = "Metadata value as defined in software module")
    private final String value;

    @JsonCreator
    public DdiMetadata(@JsonProperty("key") final String key, @JsonProperty("value")final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
