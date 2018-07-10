/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.tenancy.TenantAware;
import org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An pre-authenticated processing filter which extracts the principal from a
 * request URI and the credential from a request header in a the
 * {@link DmfTenantSecurityToken}.
 *
 */
public class ControllerPreAuthenticatedSecurityHeaderFilter extends AbstractControllerAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerPreAuthenticatedSecurityHeaderFilter.class);
    private static final Logger LOG_SECURITY_AUTH = LoggerFactory.getLogger("server-security.authentication");

    private final GetSecurityAuthorityNameTenantRunner sslIssuerNameConfigTenantRunner = new GetSecurityAuthorityNameTenantRunner();
    private final String anonymousControllerId = "anonymous";

    /**
     * Constructor.
     *
     * @param caCommonNameHeader
     *            the http-header which holds the common-name of the certificate
     * @param caAuthorityNameHeader
     *            the http-header which holds the ca-authority name of the
     *            certificate
     * @param tenantConfigurationManagement
     *            the tenant management service to retrieve configuration
     *            properties
     * @param tenantAware
     *            the tenant aware service to get configuration for the specific
     *            tenant
     * @param systemSecurityContext
     *            the system security context to get access to tenant
     *            configuration
     */
    public ControllerPreAuthenticatedSecurityHeaderFilter(final String caCommonNameHeader,
            final String caAuthorityNameHeader, final TenantConfigurationManagement tenantConfigurationManagement,
            final TenantAware tenantAware, final SystemSecurityContext systemSecurityContext) {
        super(tenantConfigurationManagement, tenantAware, systemSecurityContext);
    }

    @Override
    public HeaderAuthentication getPreAuthenticatedPrincipal(final DmfTenantSecurityToken secruityToken) {
        final String certHash = secruityToken.getHeader("X-Ssl-Client-Hash");
        final String certVerify = secruityToken.getHeader("X-Ssl-Client-Verify");
        LOGGER.error("certHash = {} certVerify = {}", certHash, certVerify);
        if (certHash == null || certVerify == null || !certVerify.equals("SUCCESS"))
            return null;
        LOGGER.error("Returning OK");
        return new HeaderAuthentication(anonymousControllerId, certHash);
    }

    @Override
    public Object getPreAuthenticatedCredentials(final DmfTenantSecurityToken secruityToken) {
        final String authorityNameConfigurationValue = tenantAware.runAsTenant(secruityToken.getTenant(),
            sslIssuerNameConfigTenantRunner);
        final List<String> knownHashes = splitMultiHashBySemicolon(authorityNameConfigurationValue);
        LOGGER.error("Valid hashes: {}, {}", authorityNameConfigurationValue, knownHashes.size());
        return knownHashes.stream().map(hashItem -> new HeaderAuthentication(anonymousControllerId, hashItem))
                .collect(Collectors.toSet());
    }

    @Override
    protected String getTenantConfigurationKey() {
        return TenantConfigurationKey.AUTHENTICATION_MODE_HEADER_ENABLED;
    }

    private final class GetSecurityAuthorityNameTenantRunner implements TenantAware.TenantRunner<String> {
        @Override
        public String run() {
            return systemSecurityContext.runAsSystem(() -> tenantConfigurationManagement.getConfigurationValue(
                    TenantConfigurationKey.AUTHENTICATION_MODE_HEADER_AUTHORITY_NAME, String.class).getValue());
        }
    }

    private static List<String> splitMultiHashBySemicolon(final String knownIssuerHashes) {
        return Arrays.stream(knownIssuerHashes.split(";|,")).map(String::toLowerCase).collect(Collectors.toList());
    }
}
