package luna.world.objects.item;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import luna.util.Manager;
import luna.world.World;
import luna.world.util.ObjectManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

// This guy will be in charge of creating new items and making itemRef objects as well
// - This will also use the database to create items
public class ItemMaker {

    // can read a json file to create itemRefs

    // create initial item refs

    public void createItemRefs(Manager m){
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
                m.call("post_addItemRef", tmp);
            }

        }catch (Exception ex){
            System.out.println("Failed to read file: " + jsonFile);
            ex.printStackTrace();
        }
        System.out.println("Created " + m.call("get_itemRefSize", null) + " item refs!");
    }

    public Item createItem(Manager m, String type){
        List<ItemRef> itemRefs = (List<ItemRef>) m.call("get_itemRefs", null);
        return createItem(itemRefs, type);
    }

    public Item createItem(String type){
        List<ItemRef> itemRefs = (List<ItemRef>) World.callManager("get_itemRefs", null);
        return createItem(itemRefs, type);
    }

    //
    public Item createItem(List<ItemRef> refs, String type){
        int id = -1;
        for(ItemRef ref : refs){
            //System.out.println(ref.getNamespace() + " == " + type);
            if(ref.getNamespace().equals(type)){
                id = ref.getItemID();
                break;
            }
        }
        if(id >= 0){
            return new Item(id);

        }

        return null;
    }


}
