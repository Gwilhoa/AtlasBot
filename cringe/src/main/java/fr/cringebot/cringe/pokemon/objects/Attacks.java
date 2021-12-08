//crit = *1,5
//crit proba = vitesse/2(*8 si boost crit) sur un choix de 255
package fr.cringebot.cringe.pokemon.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Attacks {
    public static List<Attacks> capa = new ArrayList<>();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("realname")
    @Expose
    private String realname;
    @SerializedName("power")
    @Expose
    private Integer attack = 0;
    @SerializedName("category")
    @Expose
    private String categorie = "";
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("pp")
    @Expose
    private Integer ppmax = 0;
    @SerializedName("accuracy")
    @Expose
    private Integer accuracy = 0;
    private transient int pp = ppmax;


    public Attacks() {
    }

    public static Attacks getAttackById(int i) {
        for (Attacks c : capa) {
            if (c.getId().equals(i)) {
                return c;
            }
        }
        return getAttackById(0);
    }

    public static List<Attacks> getAttByType(String t) {
        String[] ty = t.split(" ");
        ArrayList<Attacks> l = new ArrayList<>();
        for (Attacks c : capa) {
            if (c.getType().equals(ty[0]) || (ty[1] != null && c.getType().equals(ty[1]))) {
                l.add(c);
            }
        }
        if (l.isEmpty()) {
            l.add(Attacks.getAttackById(0));
        }
        return l;
    }

    public String getName() {
        return realname;
    }

    public String getType() {
        return type;
    }

    public Integer getAttack() {
        return attack;
    }

    public Integer getId() {
        return id;
    }

    public String getCategory() {
        return categorie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attacks attacks = (Attacks) o;
        return getId().equals(attacks.getId()) &&
                realname.equals(attacks.realname) &&
                getAttack().equals(attacks.getAttack()) &&
                categorie.equals(attacks.categorie) &&
                getType().equals(attacks.getType()) &&
                ppmax.equals(attacks.ppmax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), realname, getAttack(), categorie, getType(), ppmax);
    }

    @Override
    public String toString() {
        return "Attacks{" +
                "id=" + id +
                ", realname='" + realname + '\'' +
                ", attack=" + attack +
                ", categorie=" + categorie +
                ", type='" + type + '\'' +
                ", ppmax=" + ppmax +
                ", pp=" + pp +
                '}';
    }

    public Attacks copy() {
        Attacks a = getAttackById(id);
        a.pp = ppmax;
        return a;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public int getPP() {
        return pp;
    }

    public int getPPMax() {
        return ppmax;
    }
}