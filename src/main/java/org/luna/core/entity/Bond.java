package org.luna.core.entity;

class Bond {
    // TODO: this will be part of the entities memory
    //  - each bond will be updated based on the entities interaction output
    //  - will add more later
    private int entityId;
    private float bondValue; // value between 0 and 1

    public Bond(int entityId, float bondValue) {
        this.entityId = entityId;
        this.bondValue = bondValue;
        if(this.bondValue > 1) this.bondValue = 1;
        else if(this.bondValue < 0) this.bondValue = 0;
    }

    public int getEntityId() {
        return entityId;
    }

    public float getBondValue() {
        return bondValue;
    }

    public void modifyBondValue(float val){
        bondValue += val;
        if(bondValue > 1) bondValue = 1;
        else if(bondValue < 0) bondValue = 0;
    }

}
