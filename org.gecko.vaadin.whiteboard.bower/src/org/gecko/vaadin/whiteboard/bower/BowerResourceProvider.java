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

package org.gecko.vaadin.whiteboard.bower;

import java.util.Map;

import org.gecko.vaadin.whiteboard.BundleResourceProvider;
import org.gecko.vaadin.whiteboard.Constants;
import org.gecko.vaadin.whiteboard.VaadinResourceProvider;
import org.gecko.vaadin.whiteboard.annotations.VaadinResource;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

import com.vaadin.flow.server.FrontendVaadinServlet;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.startup.ServletDeployer;

/**
 * the ordinary Vaadin OSGi support, looks up resources in the clasaspath of the bundle, that registers 
 * the {@link VaadinServlet}. This is not handy in bower mode and would force us to package the bower
 * JavaScript components in the whiteboard bundle. We created that own bundle for that.
 *   
 * We provide the bower related dependencies using the {@link HttpWhiteboardResource} for the 
 * frontend folder using this service registration. 
 * So all JavaScript files will be available for the Vaadin application. In addition to that, 
 * we also make this {@link VaadinResourceProvider} as a special Vaadin Whiteboard service,
 * to be able to by-pass the resource request from the Vaadin Servlet of the application to
 * this provider. so we can bundle all bower resources in an separate bundle that is also provided
 * as part of the whiteboard packaging.
 * This is similar to what happens, when Vaadin starts the {@link ServletDeployer} an registers the 
 * {@link FrontendVaadinServlet} in an non OSGi environment.
 * 
 * We also register a capability 'vaadin.frontend' for namespace 'vaadin.osgi'. This is to wire the 
 * implementation to the whiteboard. This needs at least one of this capability.  
 * @author Mark Hoffmann
 *
 */
@Capability(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, name = Constants.VAADIN_CAPABILITY_FRONTEND, attribute = {"type=bower"})
@Component(service = VaadinResourceProvider.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, name = Constants.CM_FRONTEND)
@VaadinResource(vaadin_resource_name = Constants.BOWER_RESOURCE_NAME, vaadin_resource_prefix = Constants.BOWER_RESOURCE_PREFIX)
@HttpWhiteboardResource(prefix = "frontend/bower_components", pattern = "/frontend/bower_components/*")
public class BowerResourceProvider extends BundleResourceProvider {
	
	@Activate
	@Override
	public void activate(BundleContext context, Map<String, Object> properties) {
		super.activate(context, properties);
	}
	
}
