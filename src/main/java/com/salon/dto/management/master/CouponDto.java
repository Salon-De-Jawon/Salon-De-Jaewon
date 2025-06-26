package com.salon.dto.management.master;

import com.salon.constant.CouponType;
import com.salon.entity.management.master.Coupon;
import com.salon.entity.shop.Shop;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class CouponDto {

    private Long id; // Coupon ID

    private Long shopId; // Shop ID
    private String name;
    private int minimumAmount;
    private CouponType discountType;
    private boolean isActive;
    private LocalDate expireDate;

    public Coupon to (Shop shop) {

        Coupon coupon = new Coupon();
        coupon.setShop(shop);
        coupon.setName(this.name);
        coupon.setMinimumAmount(this.minimumAmount);
        coupon.setDiscountType(this.discountType);
        coupon.setActive(this.isActive);
        coupon.setExpireDate(this.expireDate);

        return coupon;
    }


}
