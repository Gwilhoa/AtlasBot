package fr.cringebot.cringe.pokemon.objects;


import java.awt.*;


public class Type {

    private final int id;
    private final String namefr;
    private final String name;
    private final Color c;
    private final Integer[] fb;
    private final Integer[] ef;
    private final Integer[] n;

    public Type(int id, String namefr, String name, Color c, Integer[] fb, Integer[] ef, Integer[] n) {
        this.id = id;
        this.name = name;
        this.c = c;
        this.fb = fb;
        this.ef = ef;
        this.n = n;
        this.namefr = namefr;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return c;
    }

    public Integer[] getNotEffectiveId() {
        return fb;
    }

    public Integer[] getEffectiveId() {
        return ef;
    }

    public Integer[] getNeutralId() {
        return n;
    }

    public String getNamefr() {
        return namefr;
    }

    public enum Types {
        NONE(0, "None","none",Color.WHITE,new Integer[]{0},new Integer[]{0},new Integer[]{}),
        FEU(1, "Feu","fire", Color.RED, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        PLANTE(2, "Plante","grass", Color.GREEN, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        EAU(3, "Eau","water", Color.BLUE, new Integer[]{2}, new Integer[]{1,0}, new Integer[]{0}),
        ELECTRIQUE(4, "Électrique","electric", Color.YELLOW, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        DRAGON(5, "Dragon","dragon", new Color(97, 6, 165), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        FEE(6, "Fée","fairy", Color.PINK, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        COMBAT(7, "Combat","fighting", new Color(165, 82, 57), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        GLACE(8, "Glace","ice", Color.CYAN, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        VOL(9, "Vol","flying", Color.GRAY, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        SOL(10, "Sol","ground", new Color(237, 119, 8), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        POISON(11, "Poison","poison", Color.MAGENTA, new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        SPECTRE(12, "Spectre","ghost", new Color(99, 99, 181), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        PSY(13, "Psy","psychic", new Color(255, 115, 165), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        TENEBRE(14, "Ténèbre","dark", new Color(115, 90, 74), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        ACIER(15, "Acier","steel", new Color(85, 85, 100), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        INSECTE(16, "Insecte","bug", new Color(105, 192, 128), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        ROCHE(17, "Roche","rock", new Color(189, 165, 90), new Integer[]{0}, new Integer[]{0}, new Integer[]{0}),
        NORMAL(18, "Normal","normal", Color.LIGHT_GRAY, new Integer[]{0}, new Integer[]{0}, new Integer[]{0});

        public Type getTpe() {
            return tpe;
        }

        final Type tpe;

        Types(int id, String namefr,String name, Color c, Integer[] fb, Integer[] ef, Integer[] n) {
            this.tpe = new Type(id, namefr,name, c, fb, ef, n);
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
