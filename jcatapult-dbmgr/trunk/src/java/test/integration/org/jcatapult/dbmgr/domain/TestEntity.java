package org.jcatapult.dbmgr.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.Table;

import org.joda.time.DateTime;

/**
 * Test jpa entity
 *
 * @author jhumphrey
 */
@Entity
@Table(name="test_entity")
public class TestEntity {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private int age;

    private DateTime date;

    private boolean male;

    @Column(name="insert_date")
    private DateTime timestamp;
}
