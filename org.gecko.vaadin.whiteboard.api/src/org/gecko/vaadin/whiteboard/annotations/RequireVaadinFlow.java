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

package org.gecko.vaadin.whiteboard.annotations;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.annotation.bundle.Requirements;
import org.osgi.framework.namespace.IdentityNamespace;
import org.gecko.vaadin.whiteboard.Constants;

/**
 * Requirement annotation that hold all requirements for a working setup.
 * This annotation is used by the {@link VaadinComponent} annotation. So there is no real
 * need to use this annotation in users code directly
 * @author Mark Hoffmann
 *
 */
@Requirements({
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(&(osgi.identity=com.vaadin.flow.osgi)(version=2.4.6))"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(&(osgi.identity=com.vaadin.flow.data)(version=2.4.6))"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(&(osgi.identity=com.vaadin.flow.push)(version=2.4.6))"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(&(osgi.identity=com.vaadin.flow.client)(version=2.4.6))"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(&(osgi.identity=com.vaadin.flow.server)(version=2.4.6))"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(&(osgi.identity=com.vaadin.flow.theme.material)(version=2.4.6))"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(&(osgi.identity=com.vaadin.flow.theme.lumo)(version=2.4.6))"),
	@Requirement(namespace = IdentityNamespace.IDENTITY_NAMESPACE, filter = "(&(osgi.identity=com.vaadin.flow.html.components)(version=2.4.6))"),
	@Requirement(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, filter = "(vaadin.osgi=" + Constants.VAADIN_CAPABILITY_WHITEBOARD + ")"),
	@Requirement(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, filter = "(vaadin.osgi=" + Constants.VAADIN_CAPABILITY_CLIENT + ")"),
	@Requirement(namespace = Constants.VAADIN_CAPABILITY_NAMESPACE, filter = "(vaadin.osgi=" + Constants.VAADIN_CAPABILITY_DATA + ")")
})
public @interface RequireVaadinFlow {

}
