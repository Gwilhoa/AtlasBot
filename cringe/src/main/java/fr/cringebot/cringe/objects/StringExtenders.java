/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   StringExtenders.java                               :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:22:04 by gchatain          #+#    #+#             */
/*   Updated: 2021/12/05 13:22:04 by gchatain         ###   ########lyon.fr   */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.objects;


public class StringExtenders {
    /**
     * sert à voir si String contiens search en ignorant les majs
     * @param String
     * @param Search
     * @return
     */
    public static boolean containsIgnoreCase(String s, String s2) {
        return s.toLowerCase().contains(s2.toLowerCase());
    }

    /**
     * sert a voir si String commence par String2 en ignorant les majs
     * @param String
     * @param String2
     * @return
     */
    public static boolean startWithIgnoreCase(String s, String s2) { return s.toLowerCase().startsWith(s2.toLowerCase());}

    /**
     * donner l'index de l'emplacement de où il a trouver le string2 dans string
     * @param String
     * @param String2
     * @return
     */
    public static int firstsearch(String s,String c){
        int i = 0;
        int j = 0;
        while(s.charAt(i) != 0){
            if (s.charAt(i) == c.charAt(j))
                j++;
            else
                j=0;
            if (c.length() <= j)
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
}


