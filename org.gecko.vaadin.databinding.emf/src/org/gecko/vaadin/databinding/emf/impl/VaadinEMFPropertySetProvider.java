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

package org.gecko.vaadin.databinding.emf.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.gecko.emf.osgi.EMFNamespaces;
import org.gecko.emf.osgi.EPackageConfigurator;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.vaadin.databinding.emf.EMFPropertySetProvider;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.vaadin.flow.data.binder.PropertySet;

@RequireEMF
@Component(service = EMFPropertySetProvider.class, immediate = true)
public class VaadinEMFPropertySetProvider implements EMFPropertySetProvider {

	private static final Logger logger = Logger.getLogger(VaadinEMFPropertySetProvider.class.getName());
	private static final String KEY_PATTERN = "%s#%s";
	private final Map<String, PropertySet<EObject>> propertySetMap = new ConcurrentHashMap<>();
	@Reference
	private EPackage.Registry packageRegistry;
	
	@Override
	public PropertySet<EObject> getPropertySet(EClass eclass) {
		if (eclass == null) {
			return null;
		}
		EPackage ep = eclass.getEPackage();
		String ns = ep.getNsURI();
		if (!isValidPackage(ns)) {
			removePropertySet(ns);
			logger.info("Detected invalid package. Maybe it was removed " + ns);
			return null;
		}

		String key = getKey(ep, eclass);
		if (!propertySetMap.containsKey(key)) {
			return createPropertySet(ep, eclass);
		} else {
			return propertySetMap.get(key);
		}
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addEPackageConfigurator(ServiceReference<EPackageConfigurator> configuratorRef) {
		// Nothing to do here, we just need the configuration for the remove callback
	}

	public void removeEPackageConfigurator(ServiceReference<EPackageConfigurator> configuratorRef) {
		String packageNs = (String) configuratorRef.getProperty(EMFNamespaces.EMF_MODEL_NSURI);
		if (packageNs == null) {
			logger.severe("Found EPackageConfigurator reference without ns uri property: " + configuratorRef.toString());
			return;
		}
		removePropertySet(packageNs);
	}

	private boolean isValidPackage(String ns) {
		synchronized (packageRegistry) {
			EPackage ep = packageRegistry.getEPackage(ns);
			return ep != null;
		}
	}

	private PropertySet<EObject> createPropertySet(EPackage ep, EClass eclass) {
		String key = getKey(ep, eclass);
		EPropertySet<EObject> propertySet = new EPropertySet<EObject>(eclass, this);
		propertySetMap.put(key, propertySet);
		return propertySet;
	}
	
	/**
	 * Remove {@link PropertySet} for a given package namespace
	 * @param packageNs the package namespace
	 */
	private void removePropertySet(String packageNs) {
		if (packageNs == null) {
			logger.severe("Cannot remove property set for null package namespace");
			return;
		}
		synchronized (propertySetMap) {
			Set<String> removeKeys = propertySetMap.keySet().stream().filter(key->key.startsWith(packageNs)).collect(Collectors.toSet());
			removeKeys.forEach(propertySetMap::remove);
		}
	}
	
	private static String getKey(EPackage p, EClass c) {
		if (p == null || c == null) {
			throw new IllegalArgumentException("EPackage and EClass parameters must not be null");
		}
		return String.format(KEY_PATTERN, p.getNsURI(), c.getName());
	}
	
}
