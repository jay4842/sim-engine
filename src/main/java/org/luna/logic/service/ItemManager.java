package org.luna.logic.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.luna.core.item.Item;
import org.luna.core.item.ItemRef;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.io.FileReader;
import java.util.*;

// Handle all the items created in the game
public class ItemManager implements Manager{

    public List<ItemRef> itemRefs = Collections.synchronizedList(new ArrayList<>());
    public List<Item> items = Collections.synchronizedList(new ArrayList<>());

    public ItemManager(){

    }

    @Override
    public List<ManagerCmd> update(int step, int x) {

        return null;
    }

    @Override
    public void render(int x, int step, Graphics2D g) {

    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){

    }

    public boolean reset(){
        return false;
    }

    public String getReportLine(){
        return "";
    }

    public void databasePush(){
        // TODO: create files to send to PI
    }
    //

    public void createItemRefs(){
        String jsonFile = "res/item/itemRefs.json";
        // now open the file and parse the json
        try{
            Object obj = new JSONParser().parse(new FileReader(jsonFile));
            JSONObject jo = (JSONObject) obj;
            // make array list of itemRefs
            JSONArray ja = (JSONArray) jo.get("items");
            Iterator itemIterator = ja.iterator();
            Iterator itr1;
            while (itemIterator.hasNext()){
                ItemRef tmp = new ItemRef();
                itr1 = ((Map) itemIterator.next()).entrySet().iterator();
                while (itr1.hasNext()){
                    Map.Entry pair = (Map.Entry) itr1.next();
                    String key = (String) pair.getKey();
                    switch (key){
                        case "name":
                            tmp.setName((String) pair.getValue());
                            break;
                        case "type":
                            tmp.setType((String) pair.getValue());
                            break;
                        case "properties":
                            List<String> propList = new ArrayList<>();
                            JSONArray propArr = (JSONArray) pair.getValue();
                            Iterator propItr = propArr.iterator();
                            while (propItr.hasNext())
                                propList.add((String) propItr.next());
                            tmp.setProperties(propList);
                            break;
                        case "image_path":
                            tmp.setImagePath((String) pair.getValue());
                    }
                }
                System.out.println(tmp.toString());
                System.out.println("----------------------");
            }

        }catch (Exception ex){
            System.out.println("Failed to read file: " + jsonFile);
            ex.printStackTrace();
        }

    }

}

