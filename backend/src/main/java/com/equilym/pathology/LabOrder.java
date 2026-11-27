package com.equilym.pathology;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_orders")
public class LabOrder {
    public enum Status { PENDING, COMPLETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String orderNumber;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String patientName;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "test_id", nullable = false)
    private TestMaster test;

    @NotNull
    @Column(nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(length = 200)
    private String resultValue;

    @Column(length = 1000)
    private String technicianNotes;

    private LocalDateTime completedAt;

    protected LabOrder() { }

    public LabOrder(String orderNumber, String patientName, String phone, TestMaster test, LocalDate orderDate) {
        this.orderNumber = orderNumber;
        this.patientName = patientName.trim();
        this.phone = phone.trim();
        this.test = test;
        this.orderDate = orderDate;
    }

    public void complete(String resultValue, String technicianNotes) {
        this.resultValue = resultValue.trim();
        this.technicianNotes = technicianNotes == null ? "" : technicianNotes.trim();
        this.status = Status.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getPatientName() { return patientName; }
    public String getPhone() { return phone; }
    public TestMaster getTest() { return test; }
    public LocalDate getOrderDate() { return orderDate; }
    public Status getStatus() { return status; }
    public String getResultValue() { return resultValue; }
    public String getTechnicianNotes() { return technicianNotes; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
