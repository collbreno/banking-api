package com.example.visabreno.domain;

import com.example.visabreno.domain.error.InvalidOperationTypeException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("domain")
class OperationTypeTests {

    @ParameterizedTest(name = "code {0} maps to {1} with sign {2}")
    @MethodSource("operationTypes")
    void createsOperationTypeFromCodeWithExpectedSign(
            int code,
            OperationType expectedOperationType,
            int expectedSign
    ) {
        var operationType = OperationType.fromCode(code);

        assertThat(operationType).isEqualTo(expectedOperationType);
        assertThat(operationType.code()).isEqualTo(code);
        assertThat(operationType.sign()).isEqualTo(expectedSign);
    }

    @Test
    void rejectsUnknownOperationCode() {
        assertThatThrownBy(() -> OperationType.fromCode(99))
                .isInstanceOf(InvalidOperationTypeException.class)
                .hasMessage("Invalid operation code 99");
    }

    private static Stream<Arguments> operationTypes() {
        return Stream.of(
                Arguments.of(1, OperationType.NORMAL_PURCHASE, -1),
                Arguments.of(2, OperationType.PURCHASE_WITH_INSTALLMENTS, -1),
                Arguments.of(3, OperationType.WITHDRAWAL, -1),
                Arguments.of(4, OperationType.CREDIT_VOUCHER, 1)
        );
    }
}
