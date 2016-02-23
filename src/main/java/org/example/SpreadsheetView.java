package org.example;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.example.backend.SpreadsheetEntry;
import org.example.backend.SpreadsheetService;
import org.example.csv.FileBasedUploadReceptor;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

@UIScoped
@CDIView("spreadsheet")
@ViewMenuItem(order = 3)
public class SpreadsheetView extends MVerticalLayout implements View {

    @Inject
    SpreadsheetService service;

    Header header;
    ComboBox comboBox;
    boolean maskComboboxSelectEvent;


    @PostConstruct
    void init() {
        header = new Header("Spreadsheet");

        comboBox = new ComboBox();
        comboBox.addValueChangeListener(event -> {
            if (!maskComboboxSelectEvent) {
                Object value = event.getProperty().getValue();
                if (value instanceof String) {
                    displayNoSelection();
                } else if (value instanceof SpreadsheetEntry) {
                    SpreadsheetEntry sEntry = (SpreadsheetEntry) value;
                    displaySpreadsheet(sEntry);
                }
            }
        });

        maskComboboxSelectEvent = false;
        listSpreadsheets();
        displayNoSelection();
    }

    /**
     * Spreadsheet <-> byte[]
     */
    static class SpreadsheetIO {
        public static Spreadsheet createSpreadsheet(byte[] data) {
            ByteArrayInputStream bais = new ByteArrayInputStream( data );
            try {
                return new Spreadsheet(bais);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Problem creating spreadsheet");
            }
        }

        public static byte[] saveToByteArray(Spreadsheet spreadsheet) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                spreadsheet.write(baos);
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Problem writing spreadsheet");
            }
        }
    }

    private void displayNoSelection() {
        removeAllComponents();
        setSizeUndefined();

        comboBox.setEnabled(true);
        maskComboboxSelectEvent = true; // prevent from firing events
        comboBox.select(NOTHING_SELECTED);
        maskComboboxSelectEvent = false; // prevent from firing events

        Consumer<FileBasedUploadReceptor.FileAndInfo> consumerFileAndInfo = fileAndInfo -> {
            Notification.show( "'" + fileAndInfo.getFilename() + "has been uploaded successfully.");
            byte[] data = new byte[0];
            try {
                data = Files.readAllBytes(fileAndInfo.getFile().toPath());
            } catch (IOException e) {
                throw new RuntimeException("Could not retrieve upload.");
            }
            SpreadsheetEntry spreadsheetEntry = new SpreadsheetEntry(fileAndInfo.getFilename(), data);
            service.save(spreadsheetEntry);

            listSpreadsheets();
        };

        Upload excelUpload = new Upload();
        FileBasedUploadReceptor fileBasedUploadReceptor = new FileBasedUploadReceptor(consumerFileAndInfo);
        excelUpload.setReceiver(fileBasedUploadReceptor);
        excelUpload.addSucceededListener(fileBasedUploadReceptor);
        excelUpload.setImmediate(true);
        excelUpload.setButtonCaption("Upload Excel");


        addComponent(header);
        addComponent(comboBox);
        addComponent(excelUpload);
    }

    private void displaySpreadsheet(SpreadsheetEntry entry) {
        removeAllComponents();
        setSizeFull();

        Notification.show("Loading spreadsheet '" + entry.getName() + "'.");
        Spreadsheet ss = SpreadsheetIO.createSpreadsheet(entry.getData());

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth(100, Unit.PERCENTAGE);

        HorizontalLayout saveAndDiscardHL = new HorizontalLayout();
        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            entry.setData( SpreadsheetIO.saveToByteArray(ss) );
            service.save(entry);
            Notification.show("Spreadsheet '" + entry.getName() + "' saved.");
            displayNoSelection();
        });
        Button discardButton = new Button("Discard");
        discardButton.addClickListener(event -> {
            Notification.show("Discarding spreadsheet '" + entry.getName() + "'.");
            displayNoSelection();
        });
        saveAndDiscardHL.addComponent(saveButton);
        saveAndDiscardHL.addComponent(discardButton);

        hl.addComponent(comboBox);
        hl.addComponent(saveAndDiscardHL);
        hl.setComponentAlignment(saveAndDiscardHL, Alignment.MIDDLE_RIGHT);
        hl.setExpandRatio(saveAndDiscardHL, 1);

        addComponent(header);
        comboBox.setEnabled(false);

        addComponent(hl);

        addComponent(ss);
        setExpandRatio(ss, 1);
    }

    // ==========================================================
    // ===== View related methods
    // ==========================================================

    private final String NOTHING_SELECTED = "- no Spreadsheet selected -";

    private void listSpreadsheets() {
        List<SpreadsheetEntry> entries = service.getEntries(null);
        IndexedContainer spreadsheetsContainer = new IndexedContainer();
        spreadsheetsContainer.addContainerProperty("displayname", String.class, "");
        // add empty entry
        spreadsheetsContainer.addItem(NOTHING_SELECTED).getItemProperty("displayname").setValue(NOTHING_SELECTED);

        // add entries
        for ( SpreadsheetEntry entry: entries) {
            Item item = spreadsheetsContainer.addItem(entry);
            item.getItemProperty("displayname").setValue( entry.getName() );
        }

        comboBox.setContainerDataSource(spreadsheetsContainer);
        comboBox.setTextInputAllowed(false);
        comboBox.setItemCaptionPropertyId("displayname");
        comboBox.setNullSelectionAllowed(false);
        comboBox.select(NOTHING_SELECTED);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // nothing to do on enter
    }
}
