package fr.cringebot.cringe.objects;

public class Item {
    public enum Items {
        HE("Horloge Érotique"),
        PB("Pass Brésil"),
        BF("Bouquet de fleur"),
        SB("Super Bonbon"),
        UF("Fleur"),
        CE("Chronomètre érotique"),
        BDCFU("Boite de chocolat"),
        PRFU("Flacon de parfum"),
        BRFU("Bracelet");


        private final String str;

        Items(String s) {
            this.str = s;
        }

        public String getStr() {
            return str;
        }
    }
}
