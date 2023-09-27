/**
 * Copyright (c) 2023 Bosch.IO GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.hawkbit.repository.model;

public interface Statistic {

  /**
   *
   * @return the key of the Statistic entity.
   */
  Object getName();

  /**
   *
   * @return the value of the Statistic entity.
   */
  Object getData();
}
