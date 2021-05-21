package be.vsol.vsol6.model.setting;

import be.vsol.database.annotations.Db;
import be.vsol.database.structures.DbRecord;

public abstract class Setting extends DbRecord {
    @Db public static String system_id, user_id, organization_id;
}
