package com.equilym.pathology;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

interface TestMasterRepository extends JpaRepository<TestMaster, Long> {
    List<TestMaster> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    boolean existsByCodeIgnoreCase(String code);
}

interface LabOrderRepository extends JpaRepository<LabOrder, Long> {
    List<LabOrder> findAllByOrderDateOrderByIdDesc(LocalDate orderDate);
}
