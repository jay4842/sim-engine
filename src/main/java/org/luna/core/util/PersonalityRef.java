package org.luna.core.util;

import java.security.SecureRandom;

public class PersonalityRef{
    private static SecureRandom rnd = new SecureRandom();
    private String personalityName;
    private float chance;
    private float[] extroversion;
    private float[] agreeableness;
    private float[] ambition;
    private float[] neuroticism;
    private float[] creativity;

    public PersonalityRef(){
        personalityName = "none";
        chance = 0.0f;
        extroversion = new float[2];
        agreeableness = new float[2];
        ambition = new float[2];
        neuroticism = new float[2];
        creativity = new float[2];
    }

    public String getPersonalityName() {
        return personalityName;
    }

    public float getChance() {
        return chance;
    }

    public float getExtroversionValue(){
        return extroversion[0] + rnd.nextFloat() * (extroversion[1]-extroversion[0]);
    }

    public float getAgreeablenessValue(){
        return agreeableness[0] + rnd.nextFloat() * (agreeableness[1] - agreeableness[0]);
    }

    public float getAmbitionValue(){
        return ambition[0] + rnd.nextFloat() * (ambition[1] - ambition[0]);
    }

    public float getNeuroticismValue(){
        return neuroticism[0] + rnd.nextFloat() * (neuroticism[1] - neuroticism[0]);
    }

    public float getCreativityValue(){
        return creativity[0] + rnd.nextFloat() * (creativity[1] - creativity[0]);
    }

    public void setPersonalityName(String personalityName) {
        this.personalityName = personalityName;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setExtroversion(float[] extroversion) {
        this.extroversion = extroversion;
    }

    public void setAgreeableness(float[] agreeableness) {
        this.agreeableness = agreeableness;
    }

    public void setAmbition(float[] ambition) {
        this.ambition = ambition;
    }

    public void setNeuroticism(float[] neuroticism) {
        this.neuroticism = neuroticism;
    }

    public void setCreativity(float[] creativity) {
        this.creativity = creativity;
    }
}