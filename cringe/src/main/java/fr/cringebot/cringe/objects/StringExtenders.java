package fr.cringebot.cringe.objects;


public class StringExtenders {
    public static boolean containsIgnoreCase(String s, String s2) {
        return s.toLowerCase().contains(s2.toLowerCase());
    }

    public static boolean startWithIgnoreCase(String s, String s2) { return s.toLowerCase().startsWith(s2.toLowerCase());}

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


