
package org.example.backend;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.QueryResult;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface SpreadsheetEntryRepository extends EntityRepository<SpreadsheetEntry,Long> {

    // DeltaSpike Data automatically implements this method based on naming.
    // QueryResult is a special intermedieate result type in DeltaSpike Data
    // that can be used instead of raw java.utilList. It is really handy for 
    // programmatically configuring limits, sorting and other dynamic 
    // query details.
    public QueryResult<SpreadsheetEntry> findByNameLikeIgnoreCase(String filter);

    public QueryResult<SpreadsheetEntry> findById(Long id);

}
