package org.roylance.yaorm.testmodels;

import org.roylance.yaorm.models.IEntity;

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
