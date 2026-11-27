package com.shop.backend.cart.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CartResponse {
    private String code;
    private Integer num;
    private Integer address_id;
    private String zipcode;
    private String address;
    private String address_detail;
    private List<item> items;
    private Integer total_price;
    private Integer total_discount;
    private Integer final_price;

    @Getter
    @Setter
    public static class item{
        private Integer cart_item_id;
        private Integer product_id;
        private String name;
        private Integer quantity;
        private Integer price;
        private Integer discount_rate;
        private Integer sale_price;
        private String thumbnail;

        public String getThumbnail_url() {
            return thumbnail;
        }
    }

}
