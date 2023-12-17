package net.fruitservers.deathmessages.Util;

import org.bukkit.enchantments.Enchantment;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;

public class Enchantments {

    private static final Pattern uppercaseFirstLetter = Pattern.compile("^[a-z]|_[a-z]");
    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    public static String getPrintable(Enchantment enchantment, int level) {
        String name = enchantment.getKey().getKey();
        Matcher matcher = uppercaseFirstLetter.matcher(name);
        while (matcher.find()) {
            String match = name.substring(matcher.start(), matcher.end());
            String replacement = match.toUpperCase(Locale.ROOT).replace("_", " ");
            name = name.replace(match, replacement);
        }

        // Add levels
        if (enchantment.getMaxLevel() != 1 && level < 3999) {
            name = name + " " + toRoman(level);
        } else if (enchantment.getMaxLevel() != 1 && level >= 3999) {
            name = name + " enchantment.level." + level;
        }

        return name;
    }

    /**
     * Converts an integer to roman numerals. Is capable of handling numbers greater than 3999 but 3999 is the natural limit
     * @author Ben-Hur Langoni Junior
     * @param number the number to convert
     * @return the roman numeral as a capital String
     */
    public static String toRoman(int number) {
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }

}
