package academy.request_response;

import academy.models.SubscriptionType;

public class PremiumPurchaseRequest {
    
    private SubscriptionType subscriptionType;
    private String paymentMethod;
    private String upiId;
    private String appName;
    private String couponCode;
    
    // Constructors
    public PremiumPurchaseRequest() {}
    
    public PremiumPurchaseRequest(SubscriptionType subscriptionType, String paymentMethod, 
                                  String upiId, String appName, String couponCode) {
        this.subscriptionType = subscriptionType;
        this.paymentMethod = paymentMethod;
        this.upiId = upiId;
        this.appName = appName;
        this.couponCode = couponCode;
    }
    
    // Getters and Setters
    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }
    
    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getUpiId() {
        return upiId;
    }
    
    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
    
    public String getAppName() {
        return appName;
    }
    
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}