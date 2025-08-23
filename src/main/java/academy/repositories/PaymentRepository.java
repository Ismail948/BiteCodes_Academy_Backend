package academy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.models.Payment;



public interface PaymentRepository extends JpaRepository<Payment, Long>{

	Payment findByTransactionId(String transactionId);

}
