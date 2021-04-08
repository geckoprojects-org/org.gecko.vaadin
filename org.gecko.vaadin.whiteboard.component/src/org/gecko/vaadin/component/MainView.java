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

package org.gecko.vaadin.component;

import org.gecko.vaadin.component.service.GreeterService;
import org.gecko.vaadin.whiteboard.annotations.VaadinComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
@Theme(Lumo.class)
@PWA(name = "Project Base for Vaadin Whiteboard", shortName = "Whiteboard Project Base")
@Component(service=Object.class, scope = ServiceScope.PROTOTYPE)
@VaadinComponent()
public class MainView extends VerticalLayout {
	
	private static final long serialVersionUID = 7696671599849734186L;
	@Reference
	private GreeterService greeter;
	
	@Activate
	public void activate() {
		TextField name = new TextField();
		name.setLabel("Your name to greet");
        Button button = new Button("Greet!",
                event -> {
                	String text = greeter == null ? "No greet service" : greeter.greet(name.getValue());
                	Notification.show(text);
                	});
        add(name);
        add(button);
	}

}
