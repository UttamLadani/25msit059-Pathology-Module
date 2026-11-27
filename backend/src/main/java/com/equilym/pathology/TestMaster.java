package com.equilym.pathology;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "pathology_tests")
public class TestMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String sampleType;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String normalRange;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    protected TestMaster() { }

    public TestMaster(String name, String code, String sampleType, String normalRange, BigDecimal price) {
        this.name = name.trim();
        this.code = code.trim().toUpperCase();
        this.sampleType = sampleType.trim();
        this.normalRange = normalRange.trim();
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getSampleType() { return sampleType; }
    public String getNormalRange() { return normalRange; }
    public BigDecimal getPrice() { return price; }
}
