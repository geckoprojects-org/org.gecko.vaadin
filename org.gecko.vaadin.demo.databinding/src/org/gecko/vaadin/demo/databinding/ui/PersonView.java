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

package org.gecko.vaadin.demo.databinding.ui;

import org.gecko.vaadin.databinding.emf.EMFBinder;
import org.gecko.vaadin.databinding.emf.EMFBinderFactory;
import org.gecko.vaadin.demo.databinding.PersonService;
import org.gecko.vaadin.demo.model.person.Person;
import org.gecko.vaadin.demo.model.person.PersonPackage;
import org.gecko.vaadin.whiteboard.annotations.RequireVaadinFlow;
import org.gecko.vaadin.whiteboard.annotations.VaadinApplicationSelect;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * The main view contains a button and a click listener.
 */
@Route("person")
@Theme(Lumo.class)
@RequireVaadinFlow
@Component(service=PersonView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class PersonView extends VerticalLayout {
	
	private static final long serialVersionUID = 7696671599849734186L;
	@Reference
	private PersonService personService;
	@Reference
	private EMFBinderFactory binderFactory;
	
	@Activate
	public void activate() {
		final Person person = personService.createPerson("Emil", "Tester");
		EMFBinder<Person> personBinder = binderFactory.createBinder(PersonPackage.Literals.PERSON);
		
		TextField firstName = new TextField();
		firstName.setLabel("First Name");
		personBinder.bind(firstName, PersonPackage.Literals.PERSON__FIRST_NAME);

		TextField lastName = new TextField();
		lastName.setLabel("Last Name");
		personBinder.bind(lastName, PersonPackage.Literals.PERSON__LAST_NAME);
		
        Button button = new Button("Update!",
                event -> {
                	try {
						personBinder.writeBean(person);
						Person p = personService.updatePerson(person);
						personBinder.readBean(p);
						String text = p == null ? "No one" : p.getFirstName() + " " + p.getLastName();
						Notification.show(text);
					} catch (ValidationException e) {
						Notification.show(e.getMessage());
					}
                	});
        add(firstName);
        add(lastName);
        add(button);
        personBinder.readBean(person);
	}

}
