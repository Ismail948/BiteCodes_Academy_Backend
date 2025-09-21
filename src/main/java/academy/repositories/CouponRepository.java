package academy.repositories;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import academy.models.Coupon;
import academy.models.CouponType;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    List<Coupon> findByCouponTypeAndIsActiveTrueAndEndDateAfterOrderByCreatedAtDesc(CouponType couponType, LocalDateTime currentDateTime);
    List<Coupon> findByIsActiveTrueAndEndDateAfterOrderByCreatedAtDesc(LocalDateTime currentDate);
//    List<Coupon> findByCouponTypeAndIsActiveTrueAndEndDateAfterOrderByCreatedAtDesc(Coupon couponType, LocalDateTime currentDate);
    List<Coupon> findByHolderNameOrderByCreatedAtDesc(String holderName);
}
