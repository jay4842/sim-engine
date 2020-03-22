package org.luna.logic.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.luna.core.item.Item;
import org.luna.core.item.ItemMaker;
import org.luna.core.item.ItemRef;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.io.FileReader;
import java.util.*;

// Handle all the items created in the game
public class ItemManager implements Manager{
    private ItemMaker itemMaker;
    private List<ItemRef> itemRefs = Collections.synchronizedList(new ArrayList<>());
    private Map<Integer,Item> items = Collections.synchronizedMap(new HashMap<>());
    //private Report itemLog;
    private int simId;
    public ItemManager(int sim){
        this.simId = sim;
        itemMaker = new ItemMaker();
        //itemLog = new Report("logs/item/itemReport_" + this.simId + ".txt");
    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        //itemLog.writeLn(makeItemReportLine(), step);
        return null;
    }

    @Override
    public void render(int x, int step, Graphics2D g) {

    }

    @Override
    public Object getVar(int id) {
        return items.get(id);
    }

    @Override
    public void shutdown(){
        //itemLog.closeReport();
    }

    public boolean reset(){
        return false;
    }

    public String getReportLine(int step){
        return "";
    }

    public void databasePush(){
        // TODO: create files to send to PI
    }
    //

    public Item createItem(int refID){
        // TODO, call ItemMaker to return an item of the type of refID
        Item newItem = itemMaker.createItem(itemRefs.get(refID), simId);
        newItem.setListID(items.size());
        items.put(newItem.getUniqueID(), newItem);
        return newItem;
    }

    public boolean destroyItem(int id, int step){
        //itemLog.writeLn("destroy," + id, step);
        return items.remove(id, items.get(id));
    }

    public int createItemRefs(){
        String jsonFile = "res/item/itemRefs.json";
        // now open the file and parse the json
        int count = 0;
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
                count++;
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
                            for (Object o : propArr) propList.add((String) o);
                            tmp.setProperties(propList);
                            break;
                        case "image_path":
                            tmp.setImagePath((String) pair.getValue());
                            break;
                        case "amount_bounds":
                            JSONArray amtArr = (JSONArray) pair.getValue();
                            List<Integer> intList = new ArrayList<>();
                            for (Object o : amtArr) intList.add(Integer.parseInt((String) o));
                            if(intList.size() == 2)
                                tmp.setAmtBounds(new int[]{intList.get(0), intList.get(1)});
                            else{
                                System.out.println("Error loading item!");
                                System.out.println("Error from pair value: " + pair.getValue());
                                tmp.setAmtBounds(new int[]{1,1});
                            }
                            break;

                    }
                }
                System.out.println(tmp.toString());
                itemRefs.add(tmp);
                System.out.println("----------------------");
            }

        }catch (Exception ex){
            System.out.println("Failed to read file: " + jsonFile);
            ex.printStackTrace();
        }
        return itemRefs.size();
    }

    public List<ItemRef> getItemRefs() {
        return itemRefs;
    }

    public Map<Integer,Item> getItems() {
        return items;
    }

    public String makeItemReportLine(){
        StringBuilder out = new StringBuilder();
        for(int id : items.keySet()){
            out.append(items.get(id).toString()).append("|");
        }

        return out.toString();
    }

    public void updateItem(int item, String var, Object value){
        if(var.equals("AMOUNT")){
            Item tmp = items.get(item);
            tmp.setAmount((Integer) value);
            items.put(item, tmp);
        }
        else if(var.equals("DURABILITY")){
            Item tmp = items.get(item);
            tmp.setDurability((Integer) value);
            items.put(item, tmp);
        }

    }
}

