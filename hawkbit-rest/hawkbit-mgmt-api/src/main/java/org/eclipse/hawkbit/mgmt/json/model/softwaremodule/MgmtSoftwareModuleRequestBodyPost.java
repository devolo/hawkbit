/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.mgmt.json.model.softwaremodule;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request Body for SoftwareModule POST.
 *
 */
public class MgmtSoftwareModuleRequestBodyPost {

    @JsonProperty(required = true)
    @Schema(example = "SM Name")
    private String name;

    @JsonProperty(required = true)
    @Schema(example = "1.0.0")
    private String version;

    @JsonProperty(required = true)
    @Schema(example = "os")
    private String type;

    @JsonProperty
    @Schema(example = "SM Description")
    private String description;

    @JsonProperty
    @Schema(example = "Vendor Limited, California")
    private String vendor;

    @JsonProperty
    @Schema(example = "false")
    private boolean encrypted;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     *
     * @return updated body
     */
    public MgmtSoftwareModuleRequestBodyPost setName(final String name) {
        this.name = name;
        return this;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     *
     * @return updated body
     */
    public MgmtSoftwareModuleRequestBodyPost setVersion(final String version) {
        this.version = version;
        return this;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     *
     * @return updated body
     */
    public MgmtSoftwareModuleRequestBodyPost setType(final String type) {
        this.type = type;
        return this;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     *
     * @return updated body
     */
    public MgmtSoftwareModuleRequestBodyPost setDescription(final String description) {
        this.description = description;
        return this;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @param vendor
     *            the vendor to set
     *
     * @return updated body
     */
    public MgmtSoftwareModuleRequestBodyPost setVendor(final String vendor) {
        this.vendor = vendor;
        return this;
    }

    /**
     * @return if encrypted
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * @param encrypted
     *            if should be encrypted
     *
     * @return updated body
     */
    public MgmtSoftwareModuleRequestBodyPost setEncrypted(final boolean encrypted) {
        this.encrypted = encrypted;
        return this;
    }

}
