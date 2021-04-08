/**
 * Copyright (c) 2012 - 2021 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
/*******************************************************************************
 * Copyright (c) 2021 Data In Motion Consulting GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/

package org.gecko.vaadin.whiteboard.initializer;

import java.util.List;
import java.util.Set;

import com.vaadin.flow.server.startup.ApplicationRouteRegistry;

/**
 * {@link ApplicationRouteRegistry} startup processor. A startup processor that needs
 * an {@link ApplicationRouteRegistry}
 * @author Mark Hoffmann
 *
 */
public interface ARRStartupProcessor extends StartupProcessor{
	
	/**
	 * Returns the annotations types, this processor belongs to
	 */
	public List<Class<?>> getAnnotations();
	
	/**
	 * Processes the given classes for the annotations
	 * @param classSet the set of classes
	 * @param routeRegistry the rout registry
	 */
	public void process(Set<Class<?>> classSet, ApplicationRouteRegistry routeRegistry);
	
	/**
	 * Processes the given classes for the annotations
	 * @param classSet the set of classes
	 * @param routeRegistry the rout registry
	 * @param initialize <code>true</code> for instialization
	 */
	public void process(Set<Class<?>> classSet, ApplicationRouteRegistry routeRegistry, boolean initialize);

}
