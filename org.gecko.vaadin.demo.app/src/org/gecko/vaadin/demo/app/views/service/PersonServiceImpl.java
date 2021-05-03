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

package org.gecko.vaadin.demo.app.views.service;

import org.gecko.vaadin.demo.model.person.Person;
import org.gecko.vaadin.demo.model.person.PersonFactory;
import org.osgi.service.component.annotations.Component;

@Component
public class PersonServiceImpl implements PersonService {
	
	@Override
	public Person createPerson(String name) {
		String[] splitted = name.split("\\ ");
		Person p = PersonFactory.eINSTANCE.createPerson();
		if (splitted.length >= 2) {
			p.setFirstName(splitted[0]);
			p.setLastName(splitted[1]);
			p.setId(name);
		} else {
			p.setFirstName(name);
			p.setId(name);
		}
		return p;
	}

	@Override
	public Person createPerson(String firstName, String lastName) {
		Person p = PersonFactory.eINSTANCE.createPerson();
		p.setFirstName(firstName);
		p.setLastName(lastName);
		p.setId(firstName + " " + lastName);
		return p;
	}
	
	@Override
	public Person updatePerson(Person person) {
		synchronized (person) {
			String lastName = person.getLastName();
			lastName = lastName == null ? "von Goethe" : "von " + lastName;
			person.setLastName(lastName);
		}
		return person;
	}

}
