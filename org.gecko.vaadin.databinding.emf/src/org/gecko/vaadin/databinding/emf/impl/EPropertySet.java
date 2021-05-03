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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.gecko.vaadin.databinding.emf.EMFPropertySetProvider;
import org.gecko.vaadin.databinding.emf.EPropertyDefinition;

import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertySet;

public class EPropertySet<T extends EObject> implements PropertySet<T>{
	
	private static final long serialVersionUID = -7258110647272302147L;
	private final Set<EPropertyDefinition<T, ?>> propertyDefinitions;
	private final EMFPropertySetProvider provider;
	
	public EPropertySet(EClass eClass, EMFPropertySetProvider provider) {
		this.provider = provider;
		propertyDefinitions = eClass.getEAllStructuralFeatures()
			.stream()
			.map(feature->new EPropertyDefinitionImpl<T, Object>(feature, provider))
			.collect(Collectors.toSet());
	}

	@Override
	public Stream<PropertyDefinition<T, ?>> getProperties() {
		List<PropertyDefinition<T, ?>> defs = new ArrayList<PropertyDefinition<T,?>>(propertyDefinitions);
		return defs.stream();
	}

	@Override
	public Optional<PropertyDefinition<T, ?>> getProperty(String name) {
		Optional<EPropertyDefinition<T,?>> first = propertyDefinitions.stream().filter(d->d.getName().equals(name)).findFirst();
		PropertyDefinition<T, ?> propDef = first.orElse(null);
		return Optional.ofNullable(propDef);
	}
	
}
