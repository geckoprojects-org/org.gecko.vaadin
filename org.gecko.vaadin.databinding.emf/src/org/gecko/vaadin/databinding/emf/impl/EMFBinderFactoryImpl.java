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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.gecko.vaadin.databinding.emf.EMFBinder;
import org.gecko.vaadin.databinding.emf.EMFBinderFactory;
import org.gecko.vaadin.databinding.emf.EMFPropertySetProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.data.binder.PropertySet;

@Component(scope = ServiceScope.PROTOTYPE)
public class EMFBinderFactoryImpl implements EMFBinderFactory {
	
	@Reference
	private EMFPropertySetProvider propertySetProvider;

	@SuppressWarnings("unchecked")
	@Override
	public EMFBinder<? extends EObject> createBinder(EClass eclass) {
		if (eclass == null) {
			throw new IllegalArgumentException("EClass parameter must not be null");
		}
		PropertySet<EObject> propertySet = propertySetProvider.getPropertySet(eclass);
		if (propertySet == null) {
			throw new IllegalStateException("Cannot create a binder for EClass " + eclass.getName());
		}
		return new EMFBinder<EObject>(propertySet);
	}

}
