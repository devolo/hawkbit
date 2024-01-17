/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Artifact handler properties class for holding all supported protocols with
 * host, ip, port and download pattern.
 * 
 * @see PropertyBasedArtifactUrlHandler
 */
@ConfigurationProperties("hawkbit.artifact.url")
public class ArtifactUrlHandlerProperties {

    /**
     * Rel as key and complete protocol as value.
     */
    private final Map<String, UrlProtocol> protocols = new HashMap<>();

    public Map<String, UrlProtocol> getProtocols() {
        return protocols;
    }

    /**
     * Protocol specific properties to generate URLs accordingly.
     */
    public static class UrlProtocol {

        private static final int DEFAULT_HTTP_PORT = 8080;

        /**
         * Set to true if enabled.
         */
        private boolean enabled = true;

        /**
         * Hypermedia rel value for this protocol.
         */
        private String rel = "download-http";

        /**
         * Hypermedia ref pattern for this protocol. Supported placeholders are the properties
         * supported by {@link PropertyBasedArtifactUrlHandler}.
         */
        private String ref = PropertyBasedArtifactUrlHandler.DEFAULT_URL_PROTOCOL_REF;

        /**
         * Protocol name placeholder that can be used in ref pattern.
         */
        private String protocol = "http";

        /**
         * Hostname placeholder that can be used in ref pattern.
         */
        private String hostname = "localhost";

        /**
         * IP address placeholder that can be used in ref pattern.
         */
        // Exception squid:S1313 - default only, can be configured
        @SuppressWarnings("squid:S1313")
        private String ip = "127.0.0.1";

        /**
         * Port placeholder that can be used in ref pattern.
         */
        private Integer port = DEFAULT_HTTP_PORT;

        /**
         * Support for the following hawkBit API.
         */
        private List<ApiType> supports = Arrays.asList(ApiType.DDI, ApiType.DMF, ApiType.MGMT);

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        public String getRel() {
            return rel;
        }

        public void setRel(final String rel) {
            this.rel = rel;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(final String ref) {
            this.ref = ref;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(final String hostname) {
            this.hostname = hostname;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(final String ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(final Integer port) {
            this.port = port;
        }

        public List<ApiType> getSupports() {
            return Collections.unmodifiableList(supports);
        }

        public void setSupports(final List<ApiType> supports) {
            this.supports = Collections.unmodifiableList(supports);
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(final String protocol) {
            this.protocol = protocol;
        }
    }
}
