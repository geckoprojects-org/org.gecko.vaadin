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

import java.util.Objects;
import java.util.function.BiPredicate;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.binder.RequiredFieldConfiguratorUtil;

public interface EMFRequiredFieldConfigurator extends BiPredicate<EStructuralFeature, BindingBuilder> {

	/**
	 * Configurator which is aware of {@literal @NotNull} annotation presence
	 * for a property where the default value is <code>null</code>.
	 */
	EMFRequiredFieldConfigurator NOT_NULL = (feature,
			binding) -> {return feature.getLowerBound() > 0 && 
					RequiredFieldConfiguratorUtil.testConvertedDefaultValue(binding, Objects::isNull);};

	/**
	 * Configurator which is aware of {@literal @NotEmpty} annotation presence
	 * for a property where the default value is empty.
	 */
	EMFRequiredFieldConfigurator NOT_EMPTY = (feature,
			binding) -> feature.getLowerBound() > 0 &&
					RequiredFieldConfiguratorUtil.testConvertedDefaultValue(binding,RequiredFieldConfiguratorUtil::hasZeroSize);

	/**
	 * Default configurator which is combination of {@link #NOT_NULL},
	 * {@link #NOT_EMPTY} and {@link #SIZE} configurators.
	 */
	EMFRequiredFieldConfigurator DEFAULT = NOT_NULL.chain(NOT_EMPTY);

	/**
	 * Returns a configurator that chains together this configurator with the
	 * given configurator.
	 *
	 * @param configurator
	 *            the configurator to chain, , not null
	 * @return a chained configurator
	 */
	default EMFRequiredFieldConfigurator chain(EMFRequiredFieldConfigurator configurator) {
		return (feature, binding) -> test(feature, binding)	|| configurator.test(feature, binding);
	}

}
