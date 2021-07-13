package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.vsol6.model.meta.Organization;

public class OrganizationDb extends SyncDb {

    public OrganizationDb(DbDriver driver, Organization organization) {
        super(driver, "db_" + organization.getId());
    }

}
