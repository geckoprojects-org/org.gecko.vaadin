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

package org.gecko.vaadin.demo.app.views;

import java.time.LocalDate;

import org.gecko.vaadin.databinding.emf.EMFBinder;
import org.gecko.vaadin.databinding.emf.EMFBinderFactory;
import org.gecko.vaadin.demo.app.views.service.PersonService;
import org.gecko.vaadin.demo.model.person.Person;
import org.gecko.vaadin.demo.model.person.PersonFactory;
import org.gecko.vaadin.demo.model.person.PersonPackage;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.component.checkbox.Checkbox;

@Route(value = "person-form", layout = MainView.class)
@PageTitle("Person Form")
@CssImport("resources/frontend/views/personform/person-form-view.css")
@Component(service=PersonFormView.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class PersonFormView extends Div {

    private TextField firstName;
    private TextField lastName;
    private EmailField email;
    private DatePicker dateOfBirth;
    private PhoneNumberField phone;
    private TextField occupation;

    private Button cancel;
    private Button save;
    private EMFBinder<Person> personBinder;
    
    @Reference
	private PersonService personService;
	@Reference
	private EMFBinderFactory binderFactory;

	@Activate
    public void activate() {
        addClassName("person-form-view");
        firstName = new TextField("First name");
        lastName = new TextField("Last name");
        email = new EmailField("Email address");
        dateOfBirth = new DatePicker("Birthday", LocalDate.now());
        phone = new PhoneNumberField("Phone number");
        occupation = new TextField("Occupation");
        cancel = new Button("Cancel");
        save = new Button("Save");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        final Person person = personService.createPerson("Emil", "Tester");
		personBinder = binderFactory.createBinder(PersonPackage.Literals.PERSON);
        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            personService.updatePerson(person);
            Notification.show(PersonPackage.Literals.PERSON.getName() + " details stored.");
            clearForm();
        });
    }

    private void clearForm() {
    	personBinder.setBean(PersonFactory.eINSTANCE.createPerson());
    }

    private com.vaadin.flow.component.Component createTitle() {
        return new H3("Personal information");
    }

    private com.vaadin.flow.component.Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        email.setErrorMessage("Please enter a valid email address");
        formLayout.add(firstName, lastName, dateOfBirth, phone, email, occupation);
        return formLayout;
    }

    private com.vaadin.flow.component.Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private static class PhoneNumberField extends CustomField<String> {
        private ComboBox<String> countryCode = new ComboBox<>();
        private TextField number = new TextField();

        public PhoneNumberField(String label) {
            setLabel(label);
            countryCode.setWidth("120px");
            countryCode.setPlaceholder("Country");
            countryCode.setPattern("\\+\\d*");
            countryCode.setPreventInvalidInput(true);
            countryCode.setItems("+354", "+91", "+62", "+98", "+964", "+353", "+44", "+972", "+39", "+225");
            countryCode.addCustomValueSetListener(e -> countryCode.setValue(e.getDetail()));
            number.setPattern("\\d*");
            number.setPreventInvalidInput(true);
            HorizontalLayout layout = new HorizontalLayout(countryCode, number);
            layout.setFlexGrow(1.0, number);
            add(layout);
        }

        @Override
        protected String generateModelValue() {
            if (countryCode.getValue() != null && number.getValue() != null) {
                String s = countryCode.getValue() + " " + number.getValue();
                return s;
            }
            return "";
        }

        @Override
        protected void setPresentationValue(String phoneNumber) {
            String[] parts = phoneNumber != null ? phoneNumber.split(" ", 2) : new String[0];
            if (parts.length == 1) {
                countryCode.clear();
                number.setValue(parts[0]);
            } else if (parts.length == 2) {
                countryCode.setValue(parts[0]);
                number.setValue(parts[1]);
            } else {
                countryCode.clear();
                number.clear();
            }
        }
    }

}
