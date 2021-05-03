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
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

/**
 * The link to the java script classes from the flow-client, and flow-data JAR. This content is repackaged into this bundle.
 * We create a configurable Http whiteboard resource, that is mounted from the whiteboard configurator in respect to the
 * Http whiteboards that are used. 
 * @author Mark Hoffmann
 */
@Capability(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, name = Constants.VAADIN_CAPABILITY_DATA)
@Component(service = VaadinResourceProvider.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, name = Constants.CM_DATA)
@HttpWhiteboardResource(prefix = "META-INF/resources/frontend", pattern = "/frontend/*")
public class FrontendResourceProvider extends BundleResourceProvider {
	
	@Activate
	@Override
	public void activate(BundleContext context, Map<String, Object> properties) {
		super.activate(context, properties);
	}
	
}
