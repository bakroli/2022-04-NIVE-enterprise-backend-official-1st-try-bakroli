package com.codecool.assemblyshop.testmodels;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Part {
    private Long id;
    private String name;
    private PartMaterial material;

    public Part(Long id, String name, PartMaterial material) {
        this.id = id;
        this.name = name;
        this.material = material;
    }
}
