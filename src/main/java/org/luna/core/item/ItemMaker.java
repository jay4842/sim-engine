package org.luna.core.item;

// TODO: This class will focus on building item objects from the ItemRef it belongs to
//   Will be called by the ItemManager that will manage all items/ItemRefs in the engine

import org.luna.core.util.Utility;

public class ItemMaker {
    // can read a json file to create itemRefs
    public ItemMaker(){

    }

    public Item createItem(ItemRef ref){
        Item out;

        out = new Item(ref.getItemID(), ref.getNamespace());
        // TODO: special item related calls
        //  - anomalies
        //  - amount set
        //  - other item random qualities
        int amount = ref.getAmtBounds()[0];
        out.setAmount(amount + Utility.getRnd().nextInt(ref.getAmtBounds()[1]));

        return out;
    }

}
