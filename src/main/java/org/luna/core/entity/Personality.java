package org.luna.core.entity;

public class Personality {
    // A personality will be these five traits
    // - combined they will make up an entities personality
    // - This class really is just to hold these variables
    private float extroversion;
    private float agreeableness;
    private float ambition;
    private float neuroticism;
    private float creativity;

    // neutral personality
    public Personality(){
        extroversion = 0.5f;
        agreeableness = 0.5f;
        ambition = 0.5f;
        neuroticism = 0.5f;
        creativity = 0.5f;
    }

    public Personality(float extroversion, float agreeableness, float ambition,
                       float neuroticism, float creativity){
        this.extroversion = extroversion;
        this.agreeableness = agreeableness;
        this.ambition = ambition;
        this.neuroticism = neuroticism;
        this.creativity = creativity;
    }

    public float getExtroversion() {
        return extroversion;
    }

    public float getAgreeableness() {
        return agreeableness;
    }

    public float getAmbition() {
        return ambition;
    }

    public float getNeuroticism() {
        return neuroticism;
    }

    public float getCreativity() {
        return creativity;
    }

    // modifiers
    public void addExtroversion(float f){
        extroversion += f;
        if(extroversion > 1.0f)
            extroversion = 1.0f;
        else if(extroversion < 0.0f)
            extroversion = 0.0f;
    }

    public void addAgreeableness(float f){
        agreeableness += f;
        if(agreeableness > 1.0f)
            agreeableness = 1.0f;
        else if(agreeableness < 0.0f)
            agreeableness = 0.0f;
    }

    public void addAmbition(float f){
        ambition += f;
        if(ambition > 1.0f)
            ambition = 1.0f;
        else if(ambition < 0.0f)
            ambition = 0.0f;
    }

    public void addNeuroticism(float f){
        neuroticism += f;
        if(neuroticism > 1.0f)
            neuroticism = 1.0f;
        else if(neuroticism < 0.0f)
            neuroticism = 0.0f;
    }

    public void addCreativity(float f){
        creativity += f;
        if(creativity > 1.0f)
            creativity = 1.0f;
        else if(creativity < 0.0f)
            creativity = 0.0f;
    }

}
