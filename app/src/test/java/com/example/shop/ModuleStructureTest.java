package com.example.shop;

import io.github.ilyaslabs.moduleswitch.test.ModuleRules;
import org.junit.jupiter.api.Test;

/** Checks every module rule (API shapes, no reaching into another module's -impl) without Spring. */
class ModuleStructureTest {

    @Test
    void modulesFollowTheRules() {
        ModuleRules.verify("com.example.shop");
    }
}
