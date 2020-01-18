package org.luna.logic.service;

import org.luna.core.entity.Personality;
import org.luna.core.util.ManagerCmd;

import java.awt.*;
import java.io.FileReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

// This guy will store all 16 types of personalities.
// - https://www.16personalities.com/personality-types#analysts
public class PersonalityManager implements Manager{
    private static SecureRandom rnd = new SecureRandom();
    private List<PersonalityRef> basePersonalities;

    public PersonalityManager(){
        basePersonalities = new ArrayList<>();
    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        return null;
    }

    @Override
    public void render(int x, int step, Graphics2D g) {
        //
    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown() {
        //
    }

    @Override
    public boolean reset() {
        return false;
    }

    @Override
    public String getReportLine() {
        return null;
    }

    @Override
    public void databasePush() {
        //
    }

    // will load personality refs, it will store ranges of values of each type of personality
    public int loadPersonalityFile(){
        // load json file containing all the personalities in the sim\
        String jsonFile = "res/entity/personality/personalities.json";
        try{
            JSONObject jo = (JSONObject) new JSONParser().parse(new FileReader(jsonFile));
            // make array list of itemRefs
            JSONArray ja = (JSONArray) jo.get("personalities");
            Iterator iterator = ja.iterator();
            Iterator itr1;
            for(int i = 0 ; i < ja.size(); i++){
                JSONObject obj = (JSONObject) ja.get(i);
                PersonalityRef tmpRef = new PersonalityRef();
                itr1 = obj.entrySet().iterator();
                while (itr1.hasNext()) {
                    Map.Entry pair = (Map.Entry) itr1.next();
                    String key = (String) pair.getKey();
                    key = key.toLowerCase();
                    switch (key){
                        case "chance":{
                            tmpRef.setChance((float)((double)pair.getValue()));
                            break;
                        }
                        case "name":{
                            tmpRef.setPersonalityName((String)pair.getValue());
                            break;
                        }
                        case "extroversion":{
                            JSONObject jsonObject = (JSONObject) pair.getValue();
                            float[] tmpArr = new float[]{(float)(double) jsonObject.get("min"),(float)(double) jsonObject.get("max")};
                            tmpRef.setExtroversion(tmpArr);
                            break;
                        }
                        case "agreeableness":{
                            JSONObject jsonObject = (JSONObject) pair.getValue();
                            float[] tmpArr = new float[]{(float)(double) jsonObject.get("min"),(float)(double) jsonObject.get("max")};
                            tmpRef.setAgreeableness(tmpArr);
                            break;
                        }
                        case "ambition":{
                            JSONObject jsonObject = (JSONObject) pair.getValue();
                            float[] tmpArr = new float[]{(float)(double) jsonObject.get("min"),(float)(double) jsonObject.get("max")};
                            tmpRef.setAmbition(tmpArr);
                            break;
                        }
                        case "neuroticism":{
                            JSONObject jsonObject = (JSONObject) pair.getValue();
                            float[] tmpArr = new float[]{(float)(double) jsonObject.get("min"),(float)(double) jsonObject.get("max")};
                            tmpRef.setNeuroticism(tmpArr);
                            break;
                        }
                        case "creativity":{
                            JSONObject jsonObject = (JSONObject) pair.getValue();
                            float[] tmpArr = new float[]{(float)(double) jsonObject.get("min"),(float)(double) jsonObject.get("max")};
                            tmpRef.setCreativity(tmpArr);
                            break;
                        }
                        default:{
                            System.out.println("Error, invalid key of: " + key);
                            break;
                        }
                    }
                }
                if(!tmpRef.getPersonalityName().equals("none"))
                    basePersonalities.add(tmpRef);
            }
        }catch (Exception ex) {
            System.out.println("Failed to read file: " + jsonFile);
            ex.printStackTrace();
        }

        return basePersonalities.size();
    }

    public List<PersonalityRef> getBasePersonalities() {
        return basePersonalities;
    }

    //        extroversion = 0.5f;
    //        agreeableness = 0.5f;
    //        ambition = 0.5f;
    //        neuroticism = 0.5f;
    //        creativity = 0.5f;
    public Personality makePersonality(){
        Personality p;
        int idx = rnd.nextInt(basePersonalities.size()-1);
        p = new Personality(basePersonalities.get(idx).getExtroversionValue(),
                            basePersonalities.get(idx).getAgreeablenessValue(),
                            basePersonalities.get(idx).getAmbitionValue(),
                            basePersonalities.get(idx).getNeuroticismValue(),
                            basePersonalities.get(idx).getCreativityValue());
        return p;
    }

    public Personality makePersonality(String key){
        Personality p;
        int idx = -1;
        for(int i = 0; i < basePersonalities.size(); i++){
            if(key.toLowerCase().equals(basePersonalities.get(i).getPersonalityName().toLowerCase())){
                idx = i;
                break;
            }
        }//
        if(idx != -1) {
            p = new Personality(basePersonalities.get(idx).getExtroversionValue(),
                    basePersonalities.get(idx).getAgreeablenessValue(),
                    basePersonalities.get(idx).getAmbitionValue(),
                    basePersonalities.get(idx).getNeuroticismValue(),
                    basePersonalities.get(idx).getCreativityValue());
        }else
            p = makePersonality();
        return p;
    }
}

class PersonalityRef{
    private static SecureRandom rnd = new SecureRandom();
    private String personalityName;
    private float chance;
    private float[] extroversion;
    private float[] agreeableness;
    private float[] ambition;
    private float[] neuroticism;
    private float[] creativity;

    PersonalityRef(){
        personalityName = "none";
        chance = 0.0f;
        extroversion = new float[2];
        agreeableness = new float[2];
        ambition = new float[2];
        neuroticism = new float[2];
        creativity = new float[2];
    }

    String getPersonalityName() {
        return personalityName;
    }

    float getChance() {
        return chance;
    }

    float getExtroversionValue(){
        return rnd.nextFloat() * extroversion[1] + extroversion[0];
    }

    float getAgreeablenessValue(){
        return rnd.nextFloat() * agreeableness[1] + agreeableness[0];
    }

    float getAmbitionValue(){
        return rnd.nextFloat() * ambition[1] + ambition[0];
    }

    float getNeuroticismValue(){
        return rnd.nextFloat() * neuroticism[1] + neuroticism[0];
    }

    float getCreativityValue(){
        return rnd.nextFloat() * creativity[1] + creativity[0];
    }

    void setPersonalityName(String personalityName) {
        this.personalityName = personalityName;
    }

    void setChance(float chance) {
        this.chance = chance;
    }

    void setExtroversion(float[] extroversion) {
        this.extroversion = extroversion;
    }

    void setAgreeableness(float[] agreeableness) {
        this.agreeableness = agreeableness;
    }

    void setAmbition(float[] ambition) {
        this.ambition = ambition;
    }

    void setNeuroticism(float[] neuroticism) {
        this.neuroticism = neuroticism;
    }

    void setCreativity(float[] creativity) {
        this.creativity = creativity;
    }
}