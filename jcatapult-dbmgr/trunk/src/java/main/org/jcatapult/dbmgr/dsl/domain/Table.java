package org.jcatapult.dbmgr.dsl.domain;

import java.util.List;
import java.util.ArrayList;

/**
 * @author jhumphrey
 */
public class Table {
    public String name;
    public String id;
    public List<Column> columns = new ArrayList<Column>();
}
