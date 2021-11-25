package fr.cringebot.cringe.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;

import java.util.ArrayList;


public class ReaderFile extends CSV {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(ArrayNumber.class,
            (JsonDeserializer<ArrayNumber<Integer>>) (jsonElement, type, jsonDeserializationContext) -> {
                if (!jsonElement.isJsonArray()) {
                    return new ArrayNumber<>(jsonElement.getAsNumber().intValue());
                }
                ArrayNumber<Integer> n = new ArrayNumber<>();
                JsonArray ar = jsonElement.getAsJsonArray();
                for (int i = 0; i < ar.size(); i++) {
                    n.li.add(ar.get(i).getAsNumber().intValue());
                }
                return n;
            }).create();


}

class ArrayNumber<E extends Number> {
    public ArrayList<E> li = new ArrayList<>();

    public ArrayNumber(){
    }
    public ArrayNumber(E element){
        li.add(element);
    }

    @Override
    public String toString() {
        return li.toString();
    }
}