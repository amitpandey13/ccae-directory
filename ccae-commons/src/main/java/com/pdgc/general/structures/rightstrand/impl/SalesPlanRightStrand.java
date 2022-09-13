package com.pdgc.general.structures.rightstrand.impl;

import com.pdgc.general.structures.customer.Customer;

public interface SalesPlanRightStrand extends RightStrand {
    Long getSalesWindowId();
    Customer getCustomer();
}
