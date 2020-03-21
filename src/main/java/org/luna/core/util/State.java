package org.luna.core.util;

import java.util.Map;

// states will help making data transfers easy
public interface State {
    Map<String, Object> getState();
    void updateState();
    // more later
}
