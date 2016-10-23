package com.momskitchen.momskitchen.Admin.ViewHolders;

import com.momskitchen.momskitchen.Admin.Adapters.OrderItem;
import com.momskitchen.momskitchen.model.Order;
import com.zaihuishou.expandablerecycleradapter.model.ExpandableListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 10/22/2016.
 */

public class OrderWrapper implements ExpandableListItem {
    public Order mOrder;
    public boolean expanded = false;
    List<OrderItem> orderItems;

    public OrderWrapper(Order order) {
        this.mOrder = order;
        orderItems = new ArrayList<>();
        if(this.mOrder.mealItemList!=null) {
            for (int i = 0; i < order.mealItemList.size(); i++) {
                orderItems.add(new OrderItem(
                        null,
                        null,
                        order.mealItemList.get(i),
                        order.quantities.get(i),
                        order.dates.get(i),
                        order.times.get(i),
                        order.userId,
                        order.deliveryPoint
                ));
            }
        }
    }

    @Override
    public List<?> getChildItemList() {
        return orderItems;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        expanded = isExpanded;
    }
}
