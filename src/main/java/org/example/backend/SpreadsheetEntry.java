package org.example.backend;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * A domain object example. In a real application this would probably be a JPA
 * entity or DTO.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "SpreadsheetEntry.byName", query = "SELECT e FROM SpreadsheetEntry AS e WHERE LOWER(e.name) LIKE :filter ")
})

public class SpreadsheetEntry extends AbstractEntity {

    @NotNull(message = "Name is required")
    private String name;

    private byte[] data;

    public SpreadsheetEntry(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public SpreadsheetEntry() {
        this("", new byte[0]);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
