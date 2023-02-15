package org.example.reggie.dto;


import lombok.Data;
import org.example.reggie.entity.OrderDetail;
import org.example.reggie.entity.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
