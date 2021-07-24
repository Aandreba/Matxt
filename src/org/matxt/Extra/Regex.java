package org.matxt.Extra;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static Matcher getMatcher (CharSequence value, String regex, int... flags) {
        Pattern pattern;

        if (flags.length > 0) {
            int flag = flags[0];
            for (int i=1;i<flags.length;i++) {
                flag |= flags[i];
            }

            pattern = Pattern.compile(regex, flag);
        } else {
            pattern = Pattern.compile(regex);
        }

        return pattern.matcher(value);
    }

    public static String firstMatch (CharSequence value, String regex) {
        Matcher matcher = getMatcher(value, regex);
        matcher.find();

        return matcher.group();
    }

    public static ArrayList<String> matches (CharSequence value, String regex) {
        Matcher matcher = getMatcher(value, regex);
        ArrayList<String> matches = new ArrayList<>();

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    public static String firstMatchAndDelete (StringBuilder value, String regex) {
        Matcher matcher = getMatcher(value, regex);
        matcher.find();

        int start = matcher.start();
        int end = matcher.end();

        String result = value.substring(start, end);
        value.delete(start, end);

        return result;
    }

    public static List<String> matchList (CharSequence value, String regex) {
        Matcher matcher = getMatcher(value, regex);

        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }

    public static void replace (StringBuilder builder, String regex, String replacement) {
        Matcher matcher = getMatcher(builder, regex);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            builder.replace(start, end, replacement);
        }
    }

    public static void replaceFirst (StringBuilder builder, String regex, String replacement) {
        Matcher matcher = getMatcher(builder, regex);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            builder.replace(start, end, replacement);
        }
    }
}
