package org.example;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.example.backend.PhoneBookService;
import org.example.backend.SpreadsheetService;
import org.vaadin.cdiviewmenu.ViewMenuUI;

/**
 * This is a small tutorial application for Vaadin. It also uses Vaadin CDI (so
 * deploy to Java EE server) and a dependency collection for small Java EE +
 * Vaadin applications.
 *
 * Note, that this application is just to showcase Vaadin UI development and
 * some handy utilities. Pretty much whole application is just dumped into this
 * class. For larger apps where you strive for excellent testability and
 * maintainability, you most likely want to use better structured UI code. E.g.
 * google for "Vaadin MVP pattern".
 */
//@Theme("mytheme")

// @Theme("valo")
@Theme("mytheme")
@Title("Phonebook")
@Widgetset("org.example.MyAppWidgetset")
@CDIUI("")
public class VaadinUI extends ViewMenuUI {

    @Inject
    PhoneBookService service;

//    @Inject
//    SpreadsheetService spreadsheetService;

    @PostConstruct
    void init() {
        service.ensureDemoData();
        // spreadsheetService.ensureDemoData();
    }

}
