package org.roylance.yaorm.testmodels;

import org.roylance.yaorm.models.IEntity;

public class TestModel implements IEntity {
    private String id;
    private String name;
    private String date;

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date;
    }

    public TestModel setName(String value) {
        this.name = value;
        return this;
    }

    public void setDate(String value) {
        this.date = value;
    }

    @Override
    public void setId(String value) {
        this.id = value;
    }
}
