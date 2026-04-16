package com.brunovirsan.orderflow.contracts;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContractsModuleTests {

    @Test
    void placeholderModuleExists() {
        assertThat("orderflow-contracts").isNotBlank();
    }
}
