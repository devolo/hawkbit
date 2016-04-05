/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.rest.resource.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A list representation with meta data for pagination, e.g. containing the
 * total elements and size of content. The content of the actual list is stored
 * in the {@link #content} field.
 *
 * @param <T>
 *            the type of elements in this list
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class PagedList<T> extends ResourceSupport {

    @JsonProperty
    private final List<T> content;
    @JsonProperty
    private final long totalElements;
    private final int size;

    /**
     * creates a new paged list with the given {@code content} and {@code total}
     * .
     *
     * @param content
     *            the actual content of the list
     * @param totalElements
     *            the total amount of elements
     * @throws NullPointerException
     *             in case {@code content} is {@code null}.
     */
    @JsonCreator
    public PagedList(@JsonProperty("content") @NotNull final List<T> content,
            @JsonProperty("totalElements") final long totalElements) {
        this.size = content.size();
        this.totalElements = totalElements;
        this.content = content;
    }

    /**
     * @return the size of the content list
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the total amount of elements
     */
    public long getTotal() {
        return totalElements;
    }

    public List<T> getContent() {
        return content;
    }

}
