package com.salon.dto.management.master;

import com.salon.constant.CouponType;
import com.salon.entity.management.master.Coupon;
import com.salon.entity.shop.Shop;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
public class CouponDto {

    private Long id; // Coupon ID

    private Long shopId; // Shop ID
    private String name;
    private int minimumAmount;
    private CouponType discountType;
    private int discountValue;
    private LocalDate expireDate;
    private boolean isActive;

    public Coupon to (Shop shop) {

        Coupon coupon = new Coupon();
        coupon.setShop(shop);
        coupon.setName(this.name);
        coupon.setMinimumAmount(this.minimumAmount);
        coupon.setDiscountType(this.discountType);
        coupon.setDiscountValue(this.discountValue);
        coupon.setActive(this.isActive);
        coupon.setExpireDate(this.expireDate);

        return coupon;
    }


}
