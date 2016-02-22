package org.example;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.example.backend.PhoneBookEntry;
import org.example.backend.PhoneBookService;
import org.example.backend.SpreadsheetService;
import org.example.csv.CSVReadUtil;
import org.example.csv.FileBasedUploadReceptor;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;

@UIScoped
@CDIView("spreadsheet")
@ViewMenuItem(order = 3)
public class SpreadsheetView extends VerticalLayout implements View {

    @Inject
    SpreadsheetService service;

    Header header;
    Spreadsheet spreadsheet;
//    Upload upload = new Upload();
//    Grid grid;
//    Button cancelButton;
//    Button saveButton;

    
    @PostConstruct
    void init() {


        System.out.println("Init in spreadsheet.");

        header = new Header("Spreadsheet");

        File file = new File("./Calculator.xlsx");
        try {
            spreadsheet = new Spreadsheet( file );
            // spreadsheet.setSizeFull();
            spreadsheet.setWidth(800, Unit.PIXELS);
            spreadsheet.setHeight(800, Unit.PIXELS);

            addComponent(header);
            addComponent(spreadsheet);
            // setExpandRatio(spreadsheet, 1);
            // setSizeFull();

            System.out.println("File is : " + file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error on loading file: " + file.getAbsolutePath());
        }

    }

    // ==========================================================
    // ===== View related methods
    // ==========================================================

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // nothing to do on enter
    }


}
