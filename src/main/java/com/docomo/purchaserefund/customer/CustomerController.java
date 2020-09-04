package com.docomo.purchaserefund.customer;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/customers")
public class CustomerController {
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getAllCustomers() throws PurchaseRefundException {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public Customer getCustomerById(@PathVariable("customerId") Integer customerId) throws PurchaseRefundException{
        return customerService.getCustomerById(customerId);
    }

    @GetMapping("{phoneNumber}/search-phone-number")
    public Customer getCustomerByPhoneNumber(@PathVariable("phoneNumber") String phoneNumber) throws PurchaseRefundException{
        return customerService.getCustomerByPhoneNumber(phoneNumber);
    }

    @PostMapping
    public Integer addCustomer(@Valid @RequestBody Customer customer) throws PurchaseRefundException{
        return customerService.addCustomer(customer);
    }
}
