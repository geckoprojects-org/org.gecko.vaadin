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

package org.gecko.vaadin.whiteboard.spi;

import static org.gecko.vaadin.whiteboard.Constants.CM_WHITEBOARD;
import static org.gecko.vaadin.whiteboard.Constants.REF_NAME_FRONTEND_RESOURCE;
import static org.gecko.vaadin.whiteboard.Constants.REF_NAME_REFERENCE_COLLECTOR;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_APPLICATION_CONTEXT;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_APPLICATION_NAME;
import static org.gecko.vaadin.whiteboard.Constants.VAADIN_DEFAULT_HTTP_WHITEBOARD;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;

import org.gecko.vaadin.whiteboard.VaadinResourceProvider;
import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;
import org.gecko.vaadin.whiteboard.servlet.WhiteboardVaadinServlet;
import org.gecko.vaadin.whiteboard.spi.VaadinWhiteboardConfigurator.VaadinApplicationConfig;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.InternalServerError;
import com.vaadin.flow.router.RouteNotFoundError;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, name = CM_WHITEBOARD, service = VaadinWhiteboard.class)
public class VaadinWhiteboardComponent implements VaadinDispatcher, VaadinWhiteboard {
	
	private static final Logger logger = Logger.getLogger(VaadinWhiteboardComponent.class.getName());
	@Reference(scope = ReferenceScope.PROTOTYPE)
	private VaadinWhiteboardRegistryProcessor startupProcessor;
	@Reference(name = REF_NAME_FRONTEND_RESOURCE)
	private VaadinResourceProvider frontendProvider;
	@Reference(name = REF_NAME_REFERENCE_COLLECTOR)
	private ReferenceCollector referenceCollector;
	private String applicationName;
	private String contextPath;
	private String whiteboardTarget;
	private AtomicLong changeCount = new AtomicLong(0l);
	private transient long version = 0;
	private ReentrantLock lock = new ReentrantLock();
	private ServiceRegistration<Servlet> servletRegistration;
	
	@Activate
	public void activate(VaadinApplicationConfig config, BundleContext context) throws ConfigurationException {
		configure(config);
		initializeVaadin();
		logger.info("Activating whiteboard for application " + applicationName);
		referenceCollector.connect(this);
		registerServlet(context);
	}

	@Modified
	public void modified(VaadinApplicationConfig config, BundleContext context) throws ConfigurationException {
		logger.info("Modified whiteboard for application " + applicationName);
		configure(config);
		updateServletRegistration();
	}
	
	@Deactivate
	public void deactivate(VaadinApplicationConfig config, BundleContext context) {
		logger.info("Deactivating whiteboard for application " + applicationName);
		if (servletRegistration != null) {
			servletRegistration.unregister();
		}
	}

	@Override
	public void addComponent(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		logger.info("Adding Vaadin component for application " + applicationName);
		updateComponentMap(serviceObjects, properties);
	}

	@Override
	public void modifyComponent(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		logger.info("Updating Vaadin component for application " + applicationName);
		updateComponentMap(serviceObjects, properties);
	}

	@Override
	public void removeComponent(Map<String, Object> properties) {
		logger.info("Removing Vaadin component for application " + applicationName);
		updateComponentMap(null, properties);
	}

	@Override
	public void batchDispatch() {
		if (version == startupProcessor.getServiceObjectRegistry().getVersion()) {
			return;
		} else {
			version = startupProcessor.getServiceObjectRegistry().getVersion();
		}
		if (lock.tryLock()) {
			try {
				logger.log(Level.INFO, "Dispatching routes");
				startupProcessor.process();
			} finally {
				lock.unlock();
			}
		}
		
	}
	
	private synchronized void updateComponentMap(ServiceObjects<Object> serviceObjects, Map<String, Object> properties) {
		ServiceObjectRegistry<Object> registry = startupProcessor.getServiceObjectRegistry();
		if (registry == null) {
			logger.severe("SErvice object registry is null");
			return;
		}
		if (serviceObjects != null) {
			registry.addServiceObject(serviceObjects, properties);
		} else {
			registry.removeServiceObject(properties);
		}
	}

	private void registerServlet(BundleContext context) {
		WhiteboardVaadinServlet servlet = new WhiteboardVaadinServlet(startupProcessor, frontendProvider);
		Dictionary<String, Object> properties = getServletProperties();
		servletRegistration = context.registerService(Servlet.class, servlet, properties);
	}

	private void configure(VaadinApplicationConfig config) throws ConfigurationException {
		boolean changed = false;
		String v = config.vaadin_application_name();
		if (v == null) {
			throw new ConfigurationException(VAADIN_APPLICATION_NAME, "An application name must be set");
		} 
		if (!v.equals(applicationName)) {
			applicationName = v;
			changed = true;
		}
		v = config.vaadin_application_context();
		if (v == null) {
			throw new ConfigurationException(VAADIN_APPLICATION_CONTEXT, "An application context path must be set");
		} 
		if (!v.equals(contextPath)) {
			contextPath = v;
			validate(contextPath);
			changed = true;
		}
		String target = config.vaadin_whiteboard_target();
		if (whiteboardTarget != null && 
				!whiteboardTarget.equals(target) || 
				target !=null && 
				whiteboardTarget == null) {
			whiteboardTarget = target;
			changed = true;
		}
		if (changed) {
			changeCount.incrementAndGet();
		}
	}

	/**
	 * Updates the servlet registration
	 */
	private void updateServletRegistration() {
		if (servletRegistration != null) {
			Dictionary<String, Object> properties = getServletProperties();
			servletRegistration.setProperties(properties);
		}
	}

	private void validate(String context) {
		if (context == null) {
			return;
		}
		if (!context.startsWith("/")) {
			context = "/" + context;
		}
		if (context.endsWith("/")) {
			context += "*";
		} else {
			if (!context.endsWith("/*")) {
				context += "/*";
			}
		}
	}

	private void initializeVaadin() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(HasErrorParameter.class);
		classes.add(RouteNotFoundError.class);
		classes.add(InternalServerError.class);
		startupProcessor.process(classes, true);
	}
	
	private Dictionary<String, Object> getServletProperties() {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, true);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, contextPath);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, applicationName);
		if (!VAADIN_DEFAULT_HTTP_WHITEBOARD.equals(whiteboardTarget)) {
			properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET, whiteboardTarget);
		}
		return properties;
	}

}
