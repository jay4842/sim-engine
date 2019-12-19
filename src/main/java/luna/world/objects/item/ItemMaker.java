package luna.world.objects.item;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import luna.world.util.ObjectManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

// This guy will be in charge of creating new items and making itemRef objects as well
// - This will also use the database to create items
public class ItemMaker {

    // can read a json file to create itemRefs

    // create initial item refs
    public ItemMaker(){
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
                    //System.out.println(pair.getKey() + " " + pair.getValue());
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
                    }
                }
                System.out.println(tmp.toString());
                System.out.println("----------------------");
                ObjectManager.itemRefs.add(tmp);
            }

        }catch (Exception ex){
            System.out.println("Failed to read file: " + jsonFile);
            ex.printStackTrace();
        }
        System.out.println("Created " + ObjectManager.itemRefs.size() + " item refs!");
    }

}
