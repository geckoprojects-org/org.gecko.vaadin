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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.vaadin.flow.function.ValueProvider;

public class EValueProvider<V extends EObject, T> implements ValueProvider<V, T> {

	private static final long serialVersionUID = -5025789778555018021L;
	private final EStructuralFeature feature;
	
	@SuppressWarnings("unchecked")
	public static <EO extends EObject, TYPE> EValueProvider<EO, TYPE> create(EStructuralFeature feature) {
		Class<TYPE> typeClass = (Class<TYPE>) feature.getEType().getInstanceClass();
		return new EValueProvider<EO, TYPE>(feature, typeClass);
	}
	
	public static <EO extends EObject, TYPE> EValueProvider<EO, TYPE> create(EStructuralFeature feature, Class<TYPE> typeClass) {
		return new EValueProvider<EO, TYPE>(feature, typeClass);
	}

	private EValueProvider(EStructuralFeature feature, Class<T> valueType) {
		this.feature = feature;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T apply(V source) {
		return (T) source.eGet(feature);
	}

}