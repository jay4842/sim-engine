package org.luna.core.entity;

import org.json.simple.JSONArray;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EntityUtil {

    private Map<String, Integer> stringToIntDirectionMap;
    private Map<Integer, String> intToStringDirectionMap;

    EntityUtil(){
        stringToIntDirectionMap = new HashMap<>();
        intToStringDirectionMap = new HashMap<>();
        //
        stringToIntDirectionMap.put("left", 0);
        stringToIntDirectionMap.put("right", 1);
        stringToIntDirectionMap.put("up", 2);
        stringToIntDirectionMap.put("down", 3);
        // and vise versa
        intToStringDirectionMap.put(0,"left");
        intToStringDirectionMap.put(1,"right");
        intToStringDirectionMap.put(2,"up");
        intToStringDirectionMap.put(3,"down");
    }

    Map<String, Integer> getStringToIntDirectionMap() {
        return stringToIntDirectionMap;
    }

    Map<Integer, String> getIntToStringDirectionMap() {
        return intToStringDirectionMap;
    }
}
