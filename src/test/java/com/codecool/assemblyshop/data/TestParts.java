package com.codecool.assemblyshop.data;

import com.codecool.assemblyshop.testmodels.Part;
import com.codecool.assemblyshop.testmodels.PartMaterial;

public interface TestParts {
    Part METAL_WHEEL = new Part(null, "metal wheel", PartMaterial.METAL);
    Part ROPE = new Part(null, "rope", PartMaterial.OTHER);
    Part METAL_SPRING = new Part(null, "metal spring", PartMaterial.METAL);
    Part PLASTIC_TUBE = new Part(null, "plastic tube", PartMaterial.PLASTIC);
    Part CAR_BASE = new Part(null, "car base structure", PartMaterial.METAL);
    Part CAR_MOTOR = new Part(null, "car motor", PartMaterial.COMPOSIT);
    Part WHEEL = new Part(null, "rubber wheel", PartMaterial.COMPOSIT);
    Part TOY_WHEEL = new Part(null, "toy wheel", PartMaterial.PLASTIC);
}
