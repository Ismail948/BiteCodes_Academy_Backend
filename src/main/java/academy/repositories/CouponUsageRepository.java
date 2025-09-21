// CouponUsageRepository.java
package academy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import academy.models.Coupon;
import academy.models.CouponUsage;
import academy.models.User;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    List<CouponUsage> findByUserOrderByUsedAtDesc(User user);
    List<CouponUsage> findByCouponOrderByUsedAtDesc(Coupon coupon);
    long countByUserAndCoupon(User user, Coupon coupon);
    List<CouponUsage> findByUserAndCoupon(User user, Coupon coupon);
}
