package fr.cringebot.cringe.pokemon.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DoubleString {
    @SerializedName("name")
    @Expose
    public String one;
    @SerializedName("url")
    @Expose
    public String two;

    DoubleString(String one, String two) {
        this.one = one;
        this.two = two;
    }
}
