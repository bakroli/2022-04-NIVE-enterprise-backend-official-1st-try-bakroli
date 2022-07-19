package com.codecool.assemblyshop.data;

import com.codecool.assemblyshop.testmodels.Assembly;

import java.util.List;

import static com.codecool.assemblyshop.data.TestParts.*;

public interface TestAssembly {
    Assembly CAR = new Assembly(null, "car", List.of(CAR_BASE, CAR_MOTOR, METAL_SPRING, WHEEL));
    Assembly TOY = new Assembly(null, "toy", List.of(ROPE, PLASTIC_TUBE, TOY_WHEEL));
    Assembly JUMP_ROPE = new Assembly(null, "jump rope", List.of(ROPE));
}
