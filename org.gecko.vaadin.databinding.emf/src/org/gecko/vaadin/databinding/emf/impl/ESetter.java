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

import com.vaadin.flow.data.binder.Setter;

public class ESetter<V extends EObject, T> implements Setter<V, T> {

	private static final long serialVersionUID = -751124929528581621L;
	private final EStructuralFeature feature;
	
	public static <EO extends EObject, TYPE> ESetter<EO, TYPE> create(EStructuralFeature feature) {
		return new ESetter<EO, TYPE>(feature);
	}

	private ESetter(EStructuralFeature feature) {
		this.feature = feature;
	}

	@Override
	public void accept(V bean, T fieldvalue) {
		bean.eSet(feature, fieldvalue);
	}

}