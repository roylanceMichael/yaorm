package org.roylance.yaorm.testmodels;

import org.roylance.yaorm.models.IEntity;

/**
 * Created by mikeroylance on 10/15/15.
 */
public class TestModel implements IEntity<Integer> {
    private int id;
    private String name;

    @Override
    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public TestModel setName(String value) {
        this.name = value;
        return this;
    }

    @Override
    public void setId(Integer value) {
        this.id = value;
    }
}
