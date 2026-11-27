package com.equilym.pathology;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class PathologyController {
    private final TestMasterRepository tests;
    private final LabOrderRepository orders;

    public PathologyController(TestMasterRepository tests, LabOrderRepository orders) {
        this.tests = tests;
        this.orders = orders;
    }

    @GetMapping("/tests")
    public List<TestResponse> listTests(@RequestParam(defaultValue = "") String search) {
        List<TestMaster> matches = search.isBlank()
                ? tests.findAll()
                : tests.findByNameContainingIgnoreCaseOrderByNameAsc(search.trim());
        return matches.stream().map(TestResponse::from).toList();
    }

    @PostMapping("/tests")
    @ResponseStatus(HttpStatus.CREATED)
    public TestResponse createTest(@Valid @RequestBody TestRequest request) {
        if (tests.existsByCodeIgnoreCase(request.code().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A test with that code already exists");
        }
        TestMaster test = tests.save(new TestMaster(request.name(), request.code(), request.sampleType(), request.normalRange(), request.price()));
        return TestResponse.from(test);
    }

    @GetMapping("/orders")
    public List<OrderResponse> listOrders(@RequestParam(required = false) LocalDate date) {
        LocalDate requestedDate = date == null ? LocalDate.now() : date;
        return orders.findAllByOrderDateOrderByIdDesc(requestedDate).stream().map(OrderResponse::from).toList();
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        TestMaster test = tests.findById(request.testId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Selected test was not found"));
        String orderNumber = "LAB-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LabOrder order = orders.save(new LabOrder(orderNumber, request.patientName(), request.phone(), test, request.orderDate()));
        return OrderResponse.from(order);
    }

    @PutMapping("/orders/{id}/result")
    public OrderResponse enterResult(@PathVariable Long id, @Valid @RequestBody ResultRequest request) {
        LabOrder order = orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order was not found"));
        order.complete(request.resultValue(), request.technicianNotes());
        return OrderResponse.from(orders.save(order));
    }

    @GetMapping("/orders/{id}/result")
    public OrderResponse getCompletedResult(@PathVariable Long id) {
        LabOrder order = orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order was not found"));
        if (order.getStatus() != LabOrder.Status.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This order does not have a completed result yet");
        }
        return OrderResponse.from(order);
    }
}
