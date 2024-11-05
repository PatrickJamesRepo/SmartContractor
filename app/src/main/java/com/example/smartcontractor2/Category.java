package com.example.smartcontractor2;

public class Category {
    private String name;
    private String formula;

    public Category(String name, String formula) {
        this.name = name;
        this.formula = formula;
    }

    public String getName() {
        return name;
    }

    public String getFormula() {
        return formula;
    }
}
