package me.Scyy.DeathMessages.Util;

import org.bukkit.enchantments.Enchantment;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Enchantments {

    // TODO - fix below

    private static final Pattern uppercaseFirstLetter = Pattern.compile("^[a-z]|_[a-z]");

    public static String getPrintable(Enchantment enchantment) {
        String name = enchantment.getKey().getKey();
        Matcher matcher = uppercaseFirstLetter.matcher(name);
        while (matcher.find()) {
            String match = name.substring(matcher.start(), matcher.end());
            String replacement = match.toUpperCase(Locale.ROOT).replace("_", " ");
            name = name.replace(match, replacement);
        }
        return name;
    }

}
