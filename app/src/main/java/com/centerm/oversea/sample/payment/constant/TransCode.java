package com.centerm.oversea.sample.payment.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * func：
 * author：huangmin on 2020-02-24 17:59
 * mail：huangmin_epos@centerm.com
 * tel: 18650085766
 */
public class TransCode {
    public static final String SALE = "SALE";
    public static final String REFUND = "REFUND";
    public static final String VOID = "VOID";

    public static final String SCAN = "SCAN";

    /**
     * set of transactions that need to follow full pboc flow.
     */
    public static final Set<String> FULL_PBOC = new HashSet<>();
    /**
     * set of transactions that need to be printed.
     */
    public static final Set<String> NEED_PRINT = new HashSet<>();

    static {
        FULL_PBOC.add(SALE);

        NEED_PRINT.add(SALE);
        NEED_PRINT.add(REFUND);
        NEED_PRINT.add(VOID);
    }
}
