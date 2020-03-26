package org.luna.core.item;

import org.luna.core.util.SimUtility;

public class ItemMaker {
    // can read a json file to create itemRefs
    public ItemMaker(){

    }

    public Item createItem(ItemRef ref, int sim){
        Item out;

        out = new Item(ref.getItemID(), ref.getNamespace(), sim);
        // TODO: special item related calls
        //  - anomalies
        //  - other item random qualities
        int amount = ref.getAmtBounds()[0];
        out.setAmount(amount + SimUtility.getRnd().nextInt(ref.getAmtBounds()[1]));

        return out;
    }

}
