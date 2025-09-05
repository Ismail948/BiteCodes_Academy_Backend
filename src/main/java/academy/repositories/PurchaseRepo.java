package academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.Payment;
import academy.models.Purchase;

public interface PurchaseRepo extends JpaRepository<Purchase, Long>{
	Purchase findByTransactionId(String transactionId);
}
