package org.jcatapult.dbmgr.dsl.domain;

/**
 * @author jhumphrey
 */
public class Column {
    public String name;
    public DataType type;
    boolean nullable;

    public static class DataType {
        public String type;
        public int length;
    }
}
