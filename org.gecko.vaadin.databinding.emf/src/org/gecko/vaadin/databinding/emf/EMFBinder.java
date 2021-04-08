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

package org.gecko.vaadin.databinding.emf;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.gecko.vaadin.databinding.emf.impl.EMFRequiredFieldConfigurator;
import org.gecko.vaadin.databinding.emf.impl.ESetter;
import org.gecko.vaadin.databinding.emf.impl.EValueProvider;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertySet;

public class EMFBinder<T extends EObject> extends Binder<T>{
	
//	private EMFRequiredFieldConfigurator requiredConfigurator = EMFRequiredFieldConfigurator.DEFAULT;
//	private PropertySet<T> propertySet;
	
	public EMFBinder(PropertySet<T> propertySet) {
		super(propertySet);
//		this.propertySet = propertySet;
	}
	
	public <FIELDVALUE> Binding<T, FIELDVALUE> bind(HasValue<?, FIELDVALUE> field, EStructuralFeature feature) {
		return forField(field).bind(EValueProvider.create(feature), ESetter.create(feature));
	}
	
//    @Override
//    protected BindingBuilder<T, ?> configureBinding(
//            BindingBuilder<T, ?> binding,
//            PropertyDefinition<T, ?> definition) {
//        Class<?> actualBeanType = findBeanType(beanType, definition);
//        BeanValidator validator = new BeanValidator(actualBeanType,
//                definition.getTopLevelName());
//        if (requiredConfigurator != null) {
//            configureRequired(binding, definition, validator);
//        }
//        return binding.withValidator(validator);
//    }
//    
//    private void configureRequired(BindingBuilder<T, ?> binding,
//            PropertyDefinition<T, ?> definition, BeanValidator validator) {
//        assert requiredConfigurator != null;
//        Class<?> propertyHolderType = definition.getPropertyHolderType();
//        BeanDescriptor descriptor = validator.getJavaxBeanValidator()
//                .getConstraintsForClass(propertyHolderType);
//        PropertyDescriptor propertyDescriptor = descriptor
//                .getConstraintsForProperty(definition.getTopLevelName());
//        if (propertyDescriptor == null) {
//            return;
//        }
//        if (propertyDescriptor.getConstraintDescriptors().stream()
//                .map(ConstraintDescriptor::getAnnotation)
//                .anyMatch(constraint -> requiredConfigurator.test(constraint,
//                        binding))) {
//            binding.getField().setRequiredIndicatorVisible(true);
//        }
//    }
	
}
