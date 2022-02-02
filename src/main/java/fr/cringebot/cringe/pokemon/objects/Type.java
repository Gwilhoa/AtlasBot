package fr.cringebot.cringe.pokemon.objects;


import java.awt.*;


public class Type {

    public int id;
    public String name;
    public Color c;
    public Integer[] fb;
    public Integer[] ef;
    public Integer[] n;

    public Type(int id, String name, Color c, Integer[] fb, Integer[] ef, Integer[] n) {
        this.id = id;
        this.name = name;
        this.c = c;
        this.fb = fb;
        this.ef = ef;
        this.n = n;
    }

    public enum Types {
        NONE(0,"none",Color.WHITE,new Integer[]{0},new Integer[]{0},new Integer[]{}),
        FEU(1, "fire", Color.RED, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        PLANTE(2, "grass", Color.GREEN, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        EAU(3, "water", Color.BLUE, new Integer[]{2}, new Integer[]{1,0}, new Integer[]{0}),
        ELECTRIQUE(4, "electric", Color.YELLOW, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        DRAGON(5, "dragon", new Color(97, 6, 165), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        FEE(6, "fairy", Color.PINK, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        COMBAT(7, "fighting", new Color(165, 82, 57), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        GLACE(8, "ice", Color.CYAN, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        VOL(9, "flying", Color.GRAY, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        SOL(10, "ground", new Color(237, 119, 8), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        POISON(11, "poison", Color.MAGENTA, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        SPECTRE(12, "ghost", new Color(99, 99, 181), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        PSY(13, "psychic", new Color(255, 115, 165), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        TENEBRE(14, "dark", new Color(115, 90, 74), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        ACIER(15, "steel", new Color(85, 85, 100), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        INSECTE(16, "bug", new Color(105, 192, 128), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        ROCHE(17, "rock", new Color(189, 165, 90), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        NORMAL(18, "normal", Color.LIGHT_GRAY, new Integer[]{0}, new Integer[]{0}, new Integer[]{0});

        public Type tpe;

        Types(int id, String name, Color c, Integer[] fb, Integer[] ef, Integer[] n) {
            this.tpe = new Type(id, name, c, fb, ef, n);
        }

        public static Types byName(String name) {
            for (Types g : values()) {
                if (g.tpe.name.equals(name)) {
                    return g;
                }
            }
            return Types.NORMAL;
        }
    }


}
