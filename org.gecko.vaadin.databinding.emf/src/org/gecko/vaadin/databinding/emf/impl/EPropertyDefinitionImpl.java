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

import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.gecko.vaadin.databinding.emf.EMFPropertySetProvider;
import org.gecko.vaadin.databinding.emf.EPropertyDefinition;

import com.vaadin.flow.data.binder.PropertyDefinition;
import com.vaadin.flow.data.binder.PropertySet;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;

public class EPropertyDefinitionImpl<EO extends EObject, V> implements EPropertyDefinition<EO, V> {

	private static final long serialVersionUID = -8079168794629390091L;
	private final EStructuralFeature feature;
	private final Class<V> valueTypeClass;
	private final EMFPropertySetProvider provider;

	@SuppressWarnings("unchecked")
	public EPropertyDefinitionImpl(EStructuralFeature feature, EMFPropertySetProvider provider) {
		this.feature = feature;
		this.provider = provider;
		this.valueTypeClass = (Class<V>) feature.getEType().getInstanceClass();
	}
	
	@Override
	public ValueProvider<EO, V> getGetter() {
		return EValueProvider.create(feature, valueTypeClass);
	}

	@Override
	public Optional<Setter<EO, V>> getSetter() {
		if (!feature.isChangeable()) {
			return Optional.empty();
		}
		return Optional.of(ESetter.create(feature));
	}

	@Override
	public Class<V> getType() {
		return valueTypeClass;
	}

	@Override
	public Class<?> getPropertyHolderType() {
		return getEClass().getInstanceClass();
	}

	@Override
	public String getName() {
		return feature.getName();
	}

	@Override
	public String getCaption() {
		return getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PropertySet<EO> getPropertySet() {
		if (feature instanceof EReference) {
			EReference ref = (EReference) feature;
			EClass type = ref.getEReferenceType();
			return (PropertySet<EO>) provider.getPropertySet(type);
		}
		return null;
	}

	@Override
	public PropertyDefinition<EO, ?> getParent() {
		return null;
	}

	@Override
	public EStructuralFeature getFeature() {
		return feature;
	}

	@Override
	public EClass getEClass() {
		return feature.getEContainingClass();
	}

}
