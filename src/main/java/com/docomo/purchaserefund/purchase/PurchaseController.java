package com.docomo.purchaserefund.purchase;

import com.docomo.purchaserefund.exception.PurchaseRefundException;
import com.docomo.purchaserefund.model.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/purchases")
public class PurchaseController {
    private final PurchaseService purchaseService;
    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public List<Purchase> getAllPurchases() throws PurchaseRefundException {
        return purchaseService.getAllPurchases();
    }

    @PostMapping("{customerId}/purchase")
    public Integer purchase(@PathVariable(name = "customerId") Integer customerId,
                         @Valid @RequestBody Purchase purchase) throws PurchaseRefundException {
        return purchaseService.addPurchase(customerId, purchase);
    }
}
