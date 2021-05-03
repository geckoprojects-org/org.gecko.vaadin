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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gecko.vaadin.whiteboard.annotations.StartupProcessorName;
import org.osgi.service.component.annotations.Component;

import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;

/**
 * @author mark
 * TODO DUPLICATED CODE from com.vaadin.flow.server.startup.ErrorNavigationTargetInitializer
 *
 */
@StartupProcessorName("ErrorNavigationTarget")
@Component
public class ErrorNavigationTargetProcessor implements ARRStartupProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void process(Set<Class<?>> classSet, ApplicationRouteRegistry routeRegistry) {
		if (classSet == null) {
            classSet = new HashSet<>();
        }
		Set<Class<? extends com.vaadin.flow.component.Component>> routes = classSet.stream()
                // Liberty 18 also includes the interface itself in the set...
                .filter(clazz -> clazz != HasErrorParameter.class)
                .map(clazz -> (Class<? extends com.vaadin.flow.component.Component>) clazz)
                .collect(Collectors.toSet());
        routeRegistry.setErrorNavigationTargets(routes);
	}

	@Override
	public void process(Set<Class<?>> classSet, ApplicationRouteRegistry routeRegistry, boolean initialize) {
		process(classSet, routeRegistry);
	}

	@Override
	public List<Class<?>> getAnnotations() {
		List<Class<?>> classes = new ArrayList<Class<?>>(4);
		classes.add(HasErrorParameter.class);
		return classes;
	}

	@Override
	public void process(Set<Class<?>> classSet) {
		throw new UnsupportedOperationException("This operation is not supported in this startup processor");
	}

	@Override
	public void process(Set<Class<?>> classSet, boolean initialize) {
		throw new UnsupportedOperationException("This operation is not supported in this startup processor");
	}

}
