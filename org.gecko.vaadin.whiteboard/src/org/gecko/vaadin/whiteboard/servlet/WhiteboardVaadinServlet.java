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

package org.gecko.vaadin.whiteboard.servlet;

import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.gecko.vaadin.whiteboard.VaadinResourceProvider;
import org.gecko.vaadin.whiteboard.spi.VaadinWhiteboardRegistryProcessor;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;

public class WhiteboardVaadinServlet extends VaadinServlet {

	private static final long serialVersionUID = -2046657111200016595L;
	private final VaadinWhiteboardRegistryProcessor processor;
	private final VaadinResourceProvider frontendProvider;
	
	public WhiteboardVaadinServlet(VaadinWhiteboardRegistryProcessor processor, VaadinResourceProvider frontendProvider) {
		this.processor = processor;
		this.frontendProvider = frontendProvider;
	}
	
	@Override
	protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		WhiteboardVaadinService service = new WhiteboardVaadinService(this,
                deploymentConfiguration, processor, frontendProvider);
        service.init();
        return service;
	}
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		getService().setClassLoader(getClass().getClassLoader());
	}
	
	@Override
	protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
		initParameters.setProperty("compatibilityMode", "true");
		return super.createDeploymentConfiguration(initParameters);
	}
}
