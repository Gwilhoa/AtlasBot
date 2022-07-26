package fr.cringebot.cringe.objects;

public class Item {
    public enum Items {
        HE("Horloge Érotique"),
        PB("Pass Brésil"),
        SB("Super Bonbon");


        private final String str;

        Items(String s) {
            this.str = s;
        }

        public String getStr() {
            return str;
        }
    }
}
