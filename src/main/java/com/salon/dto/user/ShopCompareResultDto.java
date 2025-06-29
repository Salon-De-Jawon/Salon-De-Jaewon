package com.salon.dto.user;

import com.salon.dto.shop.ShopListDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShopCompareResultDto {
    private Long Id; // 선택된 미용실 아이디
    private ShopListDto shopListDto;
}
