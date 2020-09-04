package com.docomo.purchaserefund.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "PURCHASE")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_generator")
    @SequenceGenerator(name = "purchase_generator", sequenceName = "purchase_seq", allocationSize = 1)
    @Column(name = "id")
    private int id;

    @Column(name = "item_description")
    private String itemDescription;

    @NotNull(message = "amount is required")
    @Column(name = "amount")
    private Double amount;

    @NotNull(message = "currency is required")
    @Size(min = 3, max = 3, message = "currency has to be 3 characters long")
    @Column(name = "currency")
    private String currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    public Purchase() {
    }

    public Purchase(
            @JsonProperty("id") int id,
            @JsonProperty("itemDescription") String itemDescription,
            @JsonProperty("amount") Double amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("customer") Customer customer,
            @JsonProperty("createdAt") Date createdAt,
            @JsonProperty("updatedAt") Date updatedAt) {
        this.id = id;
        this.itemDescription = itemDescription;
        this.amount = amount;
        this.currency = currency;
        this.customer = customer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
