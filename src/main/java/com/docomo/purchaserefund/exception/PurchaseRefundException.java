package com.docomo.purchaserefund.exception;

public class PurchaseRefundException extends Exception{
    public PurchaseRefundException(String message){
       super(message);
    }

    public PurchaseRefundException(Throwable e){
        super(e);
    }
    public PurchaseRefundException(String message,Throwable e){
        super(message,e);
    }

}
