/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   StringExtenders.java                               :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:22:04 by gchatain          #+#    #+#             */
/*   Updated: 2022/06/19 11:22:52 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.atlas.objects;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Locale;

public class StringExtenders {

    /**
     * sert à voir si un mot est contenu dans le string
     *
     * @return
     */
    public static boolean containsWord(String string, String search)
    {
        String[] words = string.split(" ");
        for (String word : words)
        {
            if (BetterIgnoreCase(word, search))
                return true;
        }
        return false;
    }
    /**
     * sert à voir si String contient search en ignorant les majs
     *
     * @param string string de base
     * @param search string a chercher
     * @return si il contient
     */
    public static boolean containsIgnoreCase(String string, String search) {
        return BetterLowCase(string).toLowerCase(Locale.ROOT).contains(BetterLowCase(search).toLowerCase(Locale.ROOT));
    }

    /**
     * sert a voir si String commence par String2 en ignorant les majs
     *
     * @param string
     * @param string2
     * @return
     */
    public static boolean startWithIgnoreCase(String string, String string2) {
        return string.toLowerCase().startsWith(string2.toLowerCase());
    }

    /**
     * donner l'index de l'emplacement de où il a trouver le string2 dans string
     *
     * @param string le string de base
     * @param search le string a chercher dans la base
     * @return
     */
    public static int firstsearch(String string, String search) {
        int i = 0;
        int j = 0;
        while (i < string.length()) {
            if (string.charAt(i) == search.charAt(j))
                j++;
            else
                j = 0;
            if (search.length() <= j)
                return i;
            i++;
        }
        return i;
    }

    /**
    condition modifié de uppercase pour la fonction rage
    */
    public static boolean isUpperCase(String s){
        int i = 0;
        int c = 0;
        while (s.length() > i){
            if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z'){
                c++;
            }
            else if (c >= 5)
                return true;
            else
                c = 0;
            i++;
        }
        return c >= 5;
    }

    private static String BetterLowCase(String str) {
        return (str.replace('ï', 'i').replace('ô', 'o').replace('é', 'e').replace('è', 'e').replace('ç', 'c').replace('É', 'e'));
    }

    public static boolean BetterIgnoreCase(String str1, String str2){
        str1 = BetterLowCase(str1);
        str2 = BetterLowCase(str2);
        return (str1.equalsIgnoreCase(str2));
    }

    public static JsonObject StringToJson(String str){
        return (new Gson().fromJson(str, JsonObject.class));
    }
}


