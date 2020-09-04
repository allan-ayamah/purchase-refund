package com.docomo.purchaserefund.refund;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Purchase;
import com.docomo.purchaserefund.model.Refund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/refunds")
public class RefundController {
    private RefundService refundService;

    @Autowired
    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @GetMapping
    public List<Refund> getAllRefunds() throws PurchaseRefundException {
        return refundService.getAllRefunds();
    }

    @GetMapping("{customerId}/customer-refunds")
    public List<Refund> getRefundsForCustomer(@PathVariable(name = "customerId") Integer customerId) throws PurchaseRefundException {
        return refundService.getRefundsForCustomer(customerId);
    }

    @PostMapping("{customerId}/refund")
    public Integer refund(@PathVariable(name = "customerId") Integer customerId, @Valid @RequestBody Refund refund) throws PurchaseRefundException {
        return refundService.addRefund(customerId, refund);
    }
}
