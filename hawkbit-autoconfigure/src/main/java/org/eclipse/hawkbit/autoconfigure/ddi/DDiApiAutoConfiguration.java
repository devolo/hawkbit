/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.autoconfigure.ddi;

import org.eclipse.hawkbit.ddi.rest.resource.DdiApiConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;


/**
 * Auto-Configuration for enabling the DDI REST-Resources.
 *
 */
@Configuration
@ConditionalOnClass(DdiApiConfiguration.class)
@Import(DdiApiConfiguration.class)
public class DDiApiAutoConfiguration {

    @Bean
    public MappingJackson2CborHttpMessageConverter mappingJackson2CborHttpMessageConverter() {
      ObjectMapper mapper = new ObjectMapper(new CBORFactory());
      Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();
      mapperBuilder.configure(mapper);
      MappingJackson2CborHttpMessageConverter converter = new MappingJackson2CborHttpMessageConverter();
      converter.setObjectMapper(mapper);
      return converter;
    }

}
