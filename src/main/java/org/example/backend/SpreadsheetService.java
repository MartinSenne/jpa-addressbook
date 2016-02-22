package org.example.backend;

import org.vaadin.viritin.LazyList;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * EJB to hide JPA related stuff from the UI layer.
 */
@Stateless
public class SpreadsheetService {

    @Inject
    SpreadsheetEntryRepository entryRepo;

    @PersistenceContext(unitName = "customerdb")
    EntityManager em;

    public SpreadsheetService() {
    }

    public void save(SpreadsheetEntry entry) {
        entryRepo.save(entry);
    }

    public List<SpreadsheetEntry> getEntries(String filter) {
        if (filter == null) {
            return entryRepo.findAll();
        }
        return entryRepo.findByNameLikeIgnoreCase("%" + filter + "%").
                getResultList();
    }

    public void delete(SpreadsheetEntry value) {
        // Hibernate cannot remove detached, reattach...
        entryRepo.remove(entryRepo.findBy(value.getId()));
    }

//    public PhoneBookEntry loadFully(PhoneBookEntry entry) {
//        // To get lazy loaded fields initialized, you have couple of options,
//        // all with pros and cons, 3 of them presented here.
//
//        // 1) use an explicit join query (with EntityManager or @Query annotation
//        //    in repository method.
//        //    em.createQuery("select e from PhoneBookEntry e LEFT JOIN FETCH e.groups where e.id = :id", PhoneBookEntry.class);
//        //    ...
//        // 2) use EntityGraph's introduced in JPA 2.1, here constructed dynamically
//        //    and passed via QueryResult object from DeltaSpike Data. You can
//        //    also use entity graphs with @Query annotation in repositories or
//        //    with raw EntityManager API.
//        EntityGraph<PhoneBookEntry> graph = this.em.createEntityGraph(
//                PhoneBookEntry.class);
//        graph.addSubgraph("groups");
//        entry = entryRepo.findById(entry.getId())
//                .hint("javax.persistence.loadgraph", graph)
//                .getSingleResult();
//
//        // 3) ..or use the infamous size() hack that all of us actually do :-)
//        entry.getAddresses().size();
//
//        return entry;
//    }

    public void ensureDemoData() {

        if (getEntries(null).isEmpty()) {
            SpreadsheetEntry entry1 = new SpreadsheetEntry("Calculator.xlsx", getFileContentAsBytes("./Calculator.xlsx"));
            SpreadsheetEntry entry2 = new SpreadsheetEntry("Demo.xlsx", getFileContentAsBytes("./Demo.xlsx"));

            em.persist(entry1);
            em.persist(entry2);
            em.flush();
        }
    }

    private byte[] getFileContentAsBytes( String filePath ) {
        Path path = Paths.get(filePath);
        try {
            byte[] data = Files.readAllBytes(path);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not read '" + filePath + "'.");
        }
    }

    /**
     * Finds a set of entries from database with given filter, starting from
     * given row. The "page size" (aka max results limit passed for the query)
     * is 45.
     *
     * @param filter
     * @param firstRow
     * @return
     */
    public List<SpreadsheetEntry> getEntriesPaged(String filter, int firstRow) {
        return entryRepo.findByNameLikeIgnoreCase("%" + filter + "%")
                .firstResult(firstRow).maxResults(LazyList.DEFAULT_PAGE_SIZE)
                .getResultList();
    }

    /**
     * Finds a number of entries from database with given filter.
     *
     * @param filter
     * @return
     */
    public int countEntries(String filter) {
        return (int) entryRepo.findByNameLikeIgnoreCase("%" + filter + "%").
                count();
    }

}
