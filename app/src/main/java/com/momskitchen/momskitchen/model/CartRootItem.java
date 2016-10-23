package com.momskitchen.momskitchen.model;

import java.util.HashMap;

/**
 * Created by hp on 9/11/2016.
 */
public class CartRootItem {
    public HashMap<String,UserCartEntry> cartEntries;

    public CartRootItem() {
    }

    public CartRootItem(HashMap<String, UserCartEntry> cartEntries) {
        this.cartEntries = cartEntries;
    }
}
