/**
 * Copyright (c) 2011-2015 Bosch Software Innovations GmbH, Germany. All rights reserved.
 */
package org.eclipse.hawkbit.mgmt.json.model.target;

import java.net.URI;

import io.swagger.v3.oas.annotations.media.Schema;
import org.eclipse.hawkbit.mgmt.json.model.MgmtNamedEntity;
import org.eclipse.hawkbit.mgmt.json.model.MgmtPollStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A json annotated rest model for Target to RESTful API representation.
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MgmtTarget extends MgmtNamedEntity {

    @JsonProperty(required = true)
    @Schema(example = "123")
    private String controllerId;

    @JsonProperty
    @Schema(example = "in_sync")
    private String updateStatus;

    @JsonProperty
    @Schema(example = "1691065941102")
    private Long lastControllerRequestAt;

    @JsonProperty
    @Schema(example = "1691065941155")
    private Long installedAt;

    @JsonProperty
    @Schema(example = "192.168.0.1")
    private String ipAddress;

    @JsonProperty
    @Schema(example = "http://192.168.0.1")
    private String address;

    @JsonProperty
    private MgmtPollStatus pollStatus;

    @JsonProperty
    @Schema(example = "38e6a19932b014040ba061795186514e")
    private String securityToken;

    @JsonProperty
    @Schema(example = "true")
    private boolean requestAttributes;

    @JsonProperty
    private boolean isCleanedUp;

    @JsonProperty
    @Schema(example = "19")
    private Long targetType;

    @JsonProperty
    @Schema(example = "defaultType")
    private String targetTypeName;

    @JsonProperty
    @Schema(example = "false")
    private Boolean autoConfirmActive;

    /**
     * @return Target type ID
     */
    public Long getTargetType() {
        return targetType;
    }

    /**
     * @param targetType
     *            Target type ID
     */
    public void setTargetType(final Long targetType) {
        this.targetType = targetType;
    }

    /**
     * @return Target type name
     */
    public String getTargetTypeName() {
        return targetTypeName;
    }

    /**
     * @param targetTypeName
     *            Target type name
     */
    public void setTargetTypeName(final String targetTypeName) {
        this.targetTypeName = targetTypeName;
    }

    /**
     * @return the controllerId
     */
    public String getControllerId() {
        return controllerId;
    }

    /**
     * @param controllerId
     *            the controllerId to set
     */
    public void setControllerId(final String controllerId) {
        this.controllerId = controllerId;
    }

    /**
     * @return the updateStatus
     */
    public String getUpdateStatus() {
        return updateStatus;
    }

    public Boolean getCleanedUp() {
        return isCleanedUp;
    }

    /**
     * @param updateStatus
     *            the updateStatus to set
     */
    public void setUpdateStatus(final String updateStatus) {
        this.updateStatus = updateStatus;
    }

    /**
     * @return the lastControllerRequestAt
     */
    public Long getLastControllerRequestAt() {
        return lastControllerRequestAt;
    }

    /**
     * @param lastControllerRequestAt
     *            the lastControllerRequestAt to set
     */
    @JsonIgnore
    public void setLastControllerRequestAt(final Long lastControllerRequestAt) {
        this.lastControllerRequestAt = lastControllerRequestAt;
    }

    /**
     * @return the installedAt
     */
    public Long getInstalledAt() {
        return installedAt;
    }

    /**
     * @param installedAt
     *            the installedAt to set
     */
    @JsonIgnore
    public void setInstalledAt(final Long installedAt) {
        this.installedAt = installedAt;
    }

    /**
     * @return the pollStatus
     */
    public MgmtPollStatus getPollStatus() {
        return pollStatus;
    }

    /**
     * @param pollStatus
     *            the pollStatus to set
     */
    @JsonIgnore
    public void setPollStatus(final MgmtPollStatus pollStatus) {
        this.pollStatus = pollStatus;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress
     *            the ipAddress to set
     */
    @JsonIgnore
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @return the securityToken
     */
    public String getSecurityToken() {
        return securityToken;
    }

    /**
     * @return Address
     */
    public String getAddress() {
        return address;
    }

    @JsonIgnore
    public void setAddress(final String address) {
        if (address != null) {
            URI.create(address);
        }
        this.address = address;
    }

    /**
     * @param securityToken
     *            the securityToken to set
     */
    @JsonIgnore
    public void setSecurityToken(final String securityToken) {
        this.securityToken = securityToken;
    }

    /**
     * @return boolean true or false
     */
    public boolean isRequestAttributes() {
        return requestAttributes;
    }

    @JsonIgnore
    public void setRequestAttributes(final boolean requestAttributes) {
        this.requestAttributes = requestAttributes;
    }

    public Boolean getAutoConfirmActive() {
        return autoConfirmActive;
    }

    @JsonIgnore
    public void setAutoConfirmActive(final boolean autoConfirmActive) {
        this.autoConfirmActive = autoConfirmActive;
    }
}
