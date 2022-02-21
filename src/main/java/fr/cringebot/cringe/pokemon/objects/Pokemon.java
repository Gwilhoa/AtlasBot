
package fr.cringebot.cringe.pokemon.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import fr.cringebot.cringe.event.BotListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Pokemon {
    public static List<Pokemon> pok;
    @SerializedName("id")
    @Expose
    private Integer id = 0;
    @SerializedName("realname")
    @Expose
    private String realname = "";
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("attack")
    @Expose
    private Integer attack = 0;
    @SerializedName("defense")
    @Expose
    private Integer defense = 0;
    @SerializedName("evolveLevel")
    @Expose
    private Integer evolveLevel = 101;
    @SerializedName("evolveTo")
    @Expose
    private BotListener.ArrayNumber<Integer> evolveTo = new BotListener.ArrayNumber<>();
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("attackspe")
    @Expose
    private Integer attspe = 0;
    @SerializedName("defensespe")
    @Expose
    private Integer defspe = 0;
    @SerializedName("speed")
    @Expose
    private Integer speed = 0;
    @SerializedName("pv")
    @Expose
    private Integer pv = 0;
    @SerializedName("levels")
    @Expose
    private List<Integer> levels = new ArrayList<>();
    @SerializedName("forms")
    @Expose
    private List<Forms> form = new ArrayList<>();
    @SerializedName("probability")
    @Expose
    private Integer probability = 0;
    @SerializedName("starter")
    @Expose
    private boolean starter = false;
    @SerializedName("attacks")
    @Expose
    private HashMap<String, Integer> attacks = new HashMap<>();

    public static ArrayList<Pokemon> getProbability(int i) {
        ArrayList<Pokemon> list = new ArrayList<>();
        for (Pokemon p : pok) {
            if (p.getProbability() == i) {
                list.add(p);
            }
        }
        return list;
    }

    public static Pokemon getRandomPokemon() {
        return pok.get(new Random().nextInt(pok.size()));
    }

    public static Pokemon getById(Integer i) {
        for (Pokemon p : pok) {
            if (p.getId().equals(i)) {
                return p;
            }
        }
        return Pokemon.getById(0);
    }

    public static Pokemon getByRealName(String name) {
        for (Pokemon p : pok) {
            if (p.getRealname().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return Pokemon.getById(0);
    }

    public List<Forms> getForm() {
        return form;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getRealname() {
        return realname;
    }

    public Integer getpv() {
        return pv;
    }

    public Integer getSpeed() {
        return speed;
    }

    public Integer getAttack() {
        return attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public Integer getEvolveLevel() {
        return evolveLevel;
    }

    public List<Integer> getEvolveTo() {
        return evolveTo.li;
    }

    public List<Type> getType() {
        ArrayList<Type> types = new ArrayList<>();
        for (String str : type.split(" "))
        {
            types.add(Type.Types.byName(str).getTpe());
        }
        return (types);
    }

    public Integer getAttackspe() {
        return attspe;
    }

    public Integer getDefensespe() {
        return defspe;
    }

    public Integer getProbability() {
        return probability;
    }

    public List<Integer> getLevels() {
        return levels == null ? new ArrayList<>() : levels;
    }

    public boolean isStarter() {
        return starter;
    }

    public Forms getDefaultForm() {
        for (Forms f : this.getForm()) {
            if (f.isdefault) {
                return f;
            }
        }
        return this.getForm().get(0);
    }

    public HashMap<Attacks, Integer> getAttacks() {
        HashMap<Attacks, Integer> att = new HashMap<>();
        for (String a : attacks.keySet()) {
            att.put(Attacks.getAttackById(Integer.parseInt(a)), attacks.get(a));
        }
        return att;
    }

    public ArrayList<Attacks> getAttDownLevel(Integer level) {
        ArrayList<Attacks> att = new ArrayList<>();
        for (Attacks a : getAttacks().keySet()) {
            if (getAttacks().get(a) <= level) {
                att.add(a);
            }
        }
        return att;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                ", id=" + id +
                ", realname=" + realname +
                ", name='" + name + '\'' +
                ", attack=" + attack +
                ", defense=" + defense +
                ", attspe=" + attspe +
                ", defspe=" + defspe +
                ", speed=" + speed +
                ", pv=" + pv +
                ", evolveLevel=" + evolveLevel +
                ", evolveTo=" + evolveTo +
                ", type='" + type + '\'' +
                ", levels=" + levels +
                ", probability=" + probability +
                ", starter =" + starter +
                ", form =" + form +
                "}\n";
    }

}

