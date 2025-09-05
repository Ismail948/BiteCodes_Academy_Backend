package academy.request_response;


public class PurchaseRequest {
    private String paymentMethod;
    private String upiId;
    private String appName;

    // Getters and Setters
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
}
