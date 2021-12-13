package fr.cringebot.cringe.pokemon.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Forms {
    @SerializedName("pokemon")
    @Expose
    public DoubleString name;
    @SerializedName("is_default")
    @Expose
    public boolean isdefault;

    Forms(boolean b, DoubleString n) {
        this.name = n;
        this.isdefault = b;
    }
}
