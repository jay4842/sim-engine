package luna.entity.util;

import java.util.List;

/*
* Groups will be how entities can work together. This will be the parent class:
*   - Subs: Parties, communities, cities etc.
* */
public class Group {
    protected List<Integer> entitiesInGroup;
    protected int leader;
    protected int[] basePos;
    protected int groupId;


}
