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

package org.gecko.vaadin.whiteboard.registry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.server.startup.ApplicationRouteRegistry;

@Component(service = {ApplicationRouteRegistry.class, ServiceObjectRegistry.class}, scope = ServiceScope.PROTOTYPE)
public class WhiteboardApplicationRouteRegistry extends ApplicationRouteRegistry implements ServiceObjectRegistry<Object> {

	private static final Logger logger = Logger.getLogger(WhiteboardApplicationRouteRegistry.class.getName());
	private static final long serialVersionUID = 1L;

	static class ServiceObjectHolder {
		public Map<String, Object> properties;
		public ServiceObjects<Object> serviceObjects;
		public Class<?> clazz;
		public String id;
		public Class<?> getClazz() {
			return clazz;
		}
	}

	private Map<Long, ServiceObjectHolder> serviceObjectMap = new ConcurrentHashMap<>();
	private Map<Long, Class<?>> componentMap = new ConcurrentHashMap<>();
	private AtomicLong changeCount = new AtomicLong(0l);

	@Deactivate
	public void deactivate() {
		serviceObjectMap.clear();
		componentMap.clear();
		changeCount.set(0);
	}

	@Override
	public void addServiceObject(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		updateComponentMap(serviceObjects, properties);
	}

	@Override
	public void updateServiceObject(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		updateComponentMap(serviceObjects, properties);
	}

	@Override
	public void removeServiceObject(Map<String, Object> properties) {
		updateComponentMap(null, properties);
	}

	@Override
	public Set<Class<?>> getClasses() {
		synchronized (serviceObjectMap) {
			return serviceObjectMap.values().stream().map(ServiceObjectHolder::getClazz).collect(Collectors.toSet());
		}
	}

	@Override
	public Object createInstance(Class<?> clazz) {
		try {
			ServiceObjectHolder holder = getHolderByClass(clazz);
			if (holder == null) {
				logger.fine("Cannot find service object holder for class " + clazz.getName());
				return null;
			}
			Object service = holder.serviceObjects.getService();
			return service;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error getting service instance for class: " + clazz.getName(), e);
		} 
		return null;
	}

	@Override
	public void releaseInstance(Object instance) {
		if (instance == null) {
			logger.warning("Cannot release a null instance");
			return;
		}
		Class<?> clazz = instance.getClass();
		try {
			ServiceObjectHolder holder = getHolderByClass(clazz);
			if (holder != null && holder.serviceObjects != null) {
				holder.serviceObjects.ungetService(instance);
			} else {
				logger.warning("Reached state where no holder or service object in holder is available");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error releasing service instance for class: " + clazz.getName(), e);
		}
	}

	@Override
	public long getVersion() {
		return changeCount.get();
	}

	@Override
	public ApplicationRouteRegistry getRouteRegistry() {
		return this;
	}

	private synchronized void updateComponentMap(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		Object service = null;
		Long serviceId = (Long) properties.get(Constants.SERVICE_ID);
		// Remove service
		if (serviceObjects == null) {
			Class<?> oldValue = componentMap.remove(serviceId);
			serviceObjectMap.remove(serviceId);
			if (oldValue != null) {
				changeCount.incrementAndGet();
			}
			return;
		}
		try {
			Class<?> newClazz;
			String[] classes = (String[]) properties.get(Constants.OBJECTCLASS);
			if (classes != null && classes.length > 0) {
				newClazz = Class.forName(classes[0]);
			} else {
				service = serviceObjects.getService();
				if (service != null) {
					try {
						newClazz = service.getClass();
					} finally {
						serviceObjects.ungetService(service);
					}
				} else {
					throw new IllegalStateException("Cannot determine a class from the service");
				}
			}
			String id = newClazz.getName() + serviceId.toString();
			ServiceObjectHolder holder = new ServiceObjectHolder();
			holder.clazz = newClazz;
			holder.id = id;
			holder.properties = properties;
			holder.serviceObjects = serviceObjects;
			Class<?> oldValue = componentMap.put(serviceId, newClazz);
			serviceObjectMap.put(serviceId, holder);
			if (!newClazz.equals(oldValue)) {
				changeCount.incrementAndGet();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error scanning component class", e);
		}
	}

	private synchronized ServiceObjectHolder getHolderByClass(Class<?> clazz) {
		return serviceObjectMap.values().stream().filter(h->h.clazz == clazz).findFirst().orElse(null);
	}

}
