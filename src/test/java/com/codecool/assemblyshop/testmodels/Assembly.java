package com.codecool.assemblyshop.testmodels;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Assembly {
    private Long id;
    private String name;
    private List<Part> parts = new ArrayList<>();

    public Assembly(Long id, String name, List<Part> parts) {

        this.id = id;
        this.name = name;
        this.parts = parts == null ? new ArrayList<>() : new ArrayList<>(parts);
    }
}
