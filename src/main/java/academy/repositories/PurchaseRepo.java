// Updated PurchaseRepo.java
package academy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import academy.models.Purchase;
import academy.models.PurchaseStatus;
import academy.models.User;

@Repository
public interface PurchaseRepo extends JpaRepository<Purchase, Long> {
    Purchase findByTransactionId(String transactionId);
    List<Purchase> findByUserOrderByPurchaseDateDesc(User user);
    List<Purchase> findByPurchaseStatus(PurchaseStatus status);
    List<Purchase> findByUserAndPurchaseStatus(User user, PurchaseStatus status);
}