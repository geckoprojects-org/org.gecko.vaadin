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

package org.gecko.vaadin.whiteboard;

public interface Constants {
	
	public static final String VAADIN_CAPABILITY_NAMESPACE = "vaadin.osgi";
	public static final String VAADIN_CAPABILITY_RESOURCE = "vaadin.resource";
	public static final String VAADIN_CAPABILITY_FRONTEND = "vaadin.frontend";
	public static final String VAADIN_CAPABILITY_CLIENT = "vaadin.client";
	public static final String VAADIN_CAPABILITY_DATA = "vaadin.data";
	public static final String VAADIN_CAPABILITY_WHITEBOARD = "vaadin.whiteboard";
	
	public static final String CM_REFERENCE_COLLECTOR = "ReferenceCollector";
	public static final String CM_WHITEBOARD = "VaadinWhiteboard";
	public static final String CM_FRONTEND = "VaadinFrontend";
	public static final String CM_CLIENT = "VaadinClient";
	public static final String CM_DATA = "VaadinData";
	public static final String CM_VAADIN_APPLICATION = "VaadinApplication";
	
	public static final String REF_NAME_FRONTEND_RESOURCE = "frontendResource";
	public static final String TARGET_NAME_FRONTEND_RESOURCE = REF_NAME_FRONTEND_RESOURCE + ".target";
	public static final String TARGET_FILTER_RESOURCE = "(&(vaadin.resource=true)%s)";

	public static final String REF_NAME_REFERENCE_COLLECTOR = "referenceCollector";
	public static final String TARGET_NAME_REFERENCE_COLLECTOR = REF_NAME_REFERENCE_COLLECTOR + ".target";
	public static final String TARGET_FILTER_REFERENCE_COLLECTOR = "(vaadin.application.name=%s)";
	
	public static final String VAADIN_PROPERTY_PREFIX = "vaadin.";
	// Vaadin Application Name
	public static final String VAADIN_APPLICATION_NAME = "vaadin.application.name";
	// Context path for Vaadin Application
	public static final String VAADIN_APPLICATION_CONTEXT = "vaadin.application.context";
	// Context path for Vaadin HTTP Whiteboard Target
	public static final String VAADIN_WHITEBOARD_TARGET = "vaadin.whiteboard.target";
	public static final String VAADIN_DEFAULT_HTTP_WHITEBOARD = "default";
	// Select filter for components to be assigned to an Vaadin application
	public static final String VAADIN_APPLICATION_SELECT = "vaadin.application.select";
	// Filter property for the component's fronted mode
	public static final String VAADIN_APPLICATION_FRONTEND = "vaadin.application.frontend";
	// Filter property for the component's service tracker
	public static final String VAADIN_COMPONENT_FILTER = "vaadin.component.filter";
	public static final String VAADIN_COMPONENT = "vaadin.component";
	public static final String VAADIN_THEME = "vaadin.themes";
	
	public static final String VAADIN_RESOURCE_PREFIX = "vaadin.resource.prefix"; 
	public static final String VAADIN_RESOURCE_NAME = "vaadin.resource.name"; 
	public static final String VAADIN_RESOURCE_DEFAULT_PREFIX = "/VAADIN-INF/resources"; 
	
	public static final String VAADIN_WHITEBOARD_FILTER_PATTERN = "(&(" + VAADIN_COMPONENT + "=true)(|(!(" + 
			VAADIN_APPLICATION_SELECT + "=*))(" + VAADIN_APPLICATION_SELECT + "=%s)))";
	
	public static final String BOWER_RESOURCE_PREFIX = "/frontend/bower_components";
	public static final String BOWER_RESOURCE_NAME = "bower";
	
	

}
