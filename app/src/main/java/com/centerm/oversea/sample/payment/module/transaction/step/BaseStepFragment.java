package com.centerm.oversea.sample.payment.module.transaction.step;

import java.util.Map;

import androidx.fragment.app.Fragment;

/**
 * @author qzhhh on 6/24/21 17:05
 */
public class BaseStepFragment extends Fragment {
    public Map<String, String> conditionMap;

    public String getNextStep(String condition) {
        if (conditionMap == null || condition == null) {
            return null;
        }
        return conditionMap.get(condition);
    }
}
