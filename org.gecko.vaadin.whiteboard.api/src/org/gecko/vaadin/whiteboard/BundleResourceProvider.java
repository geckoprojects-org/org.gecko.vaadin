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

import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author mark
 *
 */
public class BundleResourceProvider implements VaadinResourceProvider {
	
	private Bundle bundle;
	private String prefix = Constants.VAADIN_RESOURCE_DEFAULT_PREFIX;

	public void activate(BundleContext context, Map<String, Object> properties) {
		bundle = context.getBundle();
		String p = (String) properties.get(Constants.VAADIN_RESOURCE_PREFIX);
		if (p != null) {
			p = p.trim();
			if (!p.isBlank() && !p.isEmpty()) {
				if (!p.startsWith("/")) {
					p = "/" + p;
				}
				prefix = p;
			}
		}
	}

	@Override
	public URL getResource(String path) {
		if ((path != null) && 
				(bundle != null) && 
				path.startsWith(prefix)) {
			if (path.startsWith("/")) {
				path = path.substring(1);
			}

			return bundle.getEntry(path);
		}
		return null;
	}

}
