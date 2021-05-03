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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gecko.vaadin.whiteboard.VaadinResourceProvider;
import org.gecko.vaadin.whiteboard.di.OSGiInstantiator;
import org.gecko.vaadin.whiteboard.registry.ServiceObjectRegistry;
import org.gecko.vaadin.whiteboard.spi.VaadinWhiteboardRegistryProcessor;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.RouteRegistry;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.WebBrowser;
import com.vaadin.flow.theme.AbstractTheme;

public class WhiteboardVaadinService extends VaadinServletService {

	private static final long serialVersionUID = 3598055305160023933L;
	private static final Logger logger = Logger.getLogger(WhiteboardVaadinService.class.getName());
	private final ServiceObjectRegistry<Object> serviceObjectRegistry;
	private final VaadinResourceProvider frontendProvider;

	public WhiteboardVaadinService(VaadinServlet servlet,
			DeploymentConfiguration deploymentConfiguration, 
			VaadinWhiteboardRegistryProcessor processor, 
			VaadinResourceProvider frontendProvider) {
		super(servlet, deploymentConfiguration);
		this.frontendProvider = frontendProvider;
		this.serviceObjectRegistry = processor.getServiceObjectRegistry();
	}

	@Override
	protected RouteRegistry getRouteRegistry() {
		return serviceObjectRegistry.getRouteRegistry();
	}

	@Override
	protected Instantiator createInstantiator() throws ServiceException {
		return loadInstantiators().orElseGet(() -> {
			OSGiInstantiator defaultInstantiator = new OSGiInstantiator(
					this, serviceObjectRegistry);
			defaultInstantiator.init(this);
			return defaultInstantiator;
		});
	}

	@Override
	public Instantiator getInstantiator() {
		return super.getInstantiator();
	}

	@Override
	public Optional<String> getThemedUrl(String url, WebBrowser browser, AbstractTheme theme) {
		Optional<String> themedURL = super.getThemedUrl(url, browser, theme);
		/*
		 * Second try, delegating to our frontend provider resolving mechanism
		 */
		if (themedURL.isEmpty() && 
				theme != null && 
				!resolveResource(url, browser).equals(getThemedOrRawPath(url, browser, theme))) {
			return Optional.of(theme.translateUrl(url));
		}
		return Optional.empty();
	}

	/**
	 * By-passing the default mechanism and look also into our frontend provider, if no data was found using the ordniary way
	 */
	@Override
	public InputStream getResourceAsStream(String path, WebBrowser browser,
			AbstractTheme theme) {
		InputStream is = super.getResourceAsStream(path, browser, theme);
		if (is == null) {
			logger.info("Cannot get resource stream using the old way for path: " + path + ", using frontned provider instead");
			//			String resolved = resolveResource(path, browser);
			String resolved = getThemedOrRawPath(path, browser, theme);
			URL url = frontendProvider.getResource(resolved);
			if (url != null) {
				try {
					is = url.openConnection().getInputStream();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Cannot open stream for path " + resolved, e);
				}
			}
		}
		logger.info("Return resource stream " + is + " for path " + path);
		return is;
	}

	@Override
	public URL getResource(String path, WebBrowser browser, AbstractTheme theme) {
		String resolved = getThemedOrRawPath(path, browser, theme);
		URL url = frontendProvider.getResource(resolved);
		if (url == null) {
			logger.info("Cannot get resource using frontend provider" + path + ", using old way instead");
			url = super.getResource(path, browser, theme);
		}
		logger.info("Return resource URL" + (url == null ? null : url.toString()));
		return url;
	}

	/**
	 * By-passing the default mechanism and checking, if the resource is available using our
	 * frontend provider.
	 * We need this to find the webcomponentsjs/* stuff. We tell Vaadin, that we have the resource,
	 * but sadly, it is loaded over an own mechanism. So, we bundled these java script resources in the 
	 * this whiteboard bundle
	 */
	@Override
	public boolean isResourceAvailable(String url, WebBrowser browser, AbstractTheme theme) {
		boolean available = super.isResourceAvailable(url, browser, theme);
		if (!available) {
			String resolved = resolveResource(url, browser);
			URL resource = frontendProvider.getResource(resolved);
			return resource != null;
		}
		return available;
	}

	/**
	 * Copied method from VaadinServletService#getThemedOrRawPath. 
	 * If was extended to hook into our frontend provider, to resolve the 
	 * themed path's using this instead of the callers classloader.  
	 * TODO Make that method 'protected' instead of 'private', to allow  custom 
	 * extension
	 * @return @see VaadinServletService#getThemedOrRawPath
	 */
	private String getThemedOrRawPath(String url, WebBrowser browser,
			AbstractTheme theme) {
		String resourcePath = resolveResource(url, browser);

		Optional<String> themeResourcePath = getThemeResourcePath(resourcePath,
				theme);
		if (themeResourcePath.isPresent()) {
			/*
			 * OSGi first, check resource at frontend provider
			 */
			URL themeResource = frontendProvider.getResource(themeResourcePath.get());
			if (themeResource != null) {
				return themeResourcePath.get();
			}
			/*
			 * Last resort, check frontend provider
			 */
			themeResource = getResourceInServletContextOrWebJar(
					themeResourcePath.get());
			if (themeResource != null) {
				return themeResourcePath.get();
			}
		}
		return resourcePath;
	}

	/**
	 * Duplicated method from VaadinServletService#getThemeResourcePath.
	 * If the VaadinServletService#getThemedOrRawPath would be protected,
	 * this method could stay as it is.
	 * @param path the resource path
	 * @param theme the theme
	 * @return the translated themed path or empty
	 */
	private Optional<String> getThemeResourcePath(String path,
			AbstractTheme theme) {
		if (theme == null) {
			return Optional.empty();
		}
		String themeUrl = theme.translateUrl(path);
		if (path.equals(themeUrl)) {
			return Optional.empty();
		}

		return Optional.of(themeUrl);
	}

}
