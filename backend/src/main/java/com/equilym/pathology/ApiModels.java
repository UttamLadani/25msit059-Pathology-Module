package com.equilym.pathology;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

record TestRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 80) String sampleType,
        @NotBlank @Size(max = 120) String normalRange,
        @NotNull @DecimalMin("0.0") BigDecimal price) { }

record TestResponse(Long id, String name, String code, String sampleType, String normalRange, BigDecimal price) {
    static TestResponse from(TestMaster test) {
        return new TestResponse(test.getId(), test.getName(), test.getCode(), test.getSampleType(), test.getNormalRange(), test.getPrice());
    }
}

record OrderRequest(
        @NotBlank @Size(max = 120) String patientName,
        @NotBlank @Size(max = 30) String phone,
        @NotNull @Positive Long testId,
        @NotNull LocalDate orderDate) { }

record ResultRequest(@NotBlank @Size(max = 200) String resultValue, @Size(max = 1000) String technicianNotes) { }

record OrderResponse(Long id, String orderNumber, String patientName, String phone, LocalDate orderDate,
                     Long testId, String testName, String testCode, String sampleType, String normalRange,
                     BigDecimal price, String status, String resultValue, String technicianNotes, LocalDateTime completedAt) {
    static OrderResponse from(LabOrder order) {
        TestMaster test = order.getTest();
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getPatientName(), order.getPhone(),
                order.getOrderDate(), test.getId(), test.getName(), test.getCode(), test.getSampleType(),
                test.getNormalRange(), test.getPrice(), order.getStatus().name(), order.getResultValue(),
                order.getTechnicianNotes(), order.getCompletedAt());
    }
}
