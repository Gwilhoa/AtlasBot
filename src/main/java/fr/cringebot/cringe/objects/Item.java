package fr.cringebot.cringe.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Item {

    public enum type {
        WaifuConsumable("Objet d'affection", 1),
        WaifuItem("Objet concernant les Waifus", 2),
        PokemonItem("Objet concernant les Pokemon", 3),
        Other("Objet général", 4);

        private final String name;
        private final Integer id;
        type(String name, Integer id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public Integer getId() {
            return id;
        }

        public static type getTypeById(int id) {
            for (type it : type.values()) {
                if (it.getId().equals(id))
                    return it;
            }
            return null;
        }
    }
    public enum Items {
        PB("Pass Brésil", 1, 5, Item.type.Other),
        CEFU("Chronomètre érotique",2, 20, Item.type.WaifuItem),
        HEFU("Horloge Érotique", 3, 100, Item.type.WaifuItem),
        UFFU("Fleur", 4, -1, Item.type.WaifuItem),
        BFFU("Bouquet de fleur", 5, 10, Item.type.WaifuConsumable),
        BDCFU("Boite de chocolat",6,40, Item.type.WaifuConsumable),
        PRFU("Flacon de parfum", 7,80, Item.type.WaifuConsumable),
        BRFU("Bracelet", 8, 130, Item.type.WaifuConsumable),
        COLLFU("Collier", 9, -1, Item.type.WaifuConsumable),
        BGEFU("Bague", 10, -1, Item.type.WaifuConsumable),
        SBPKM("Super bonbon", 11, -1, Item.type.WaifuConsumable);


        private final String name;
        private final Integer id;
        private final Integer price;
        private final Item.type type;

        Items(String name, Integer id, Integer price, Item.type tpe) {
            this.name = name;
            this.id = id;
            this.price = price;
            this.type = tpe;
        }

        public Integer getPrice() {
            return price;
        }

        public Item.type getType() {
            return type;
        }

        public String getName() {
            return name;
        }
        public Integer getId() {return id;}
        public static Items getItemById(int id) {
            for (Items it : Items.values()) {
                if (it.getId().equals(id))
                    return it;
            }
            return null;
        }
        public static ArrayList<Items> getItems() {
            return new ArrayList<Items>(List.of(Items.values()));
        }

        public static ArrayList<Items> getItemByType(type tpe)
        {
            ArrayList<Items> ret = new ArrayList();
            for (Items item : getItems())
            {
                if (item.type.equals(tpe))
                    ret.add(item);
            }
            return ret;
        }
    }
}
