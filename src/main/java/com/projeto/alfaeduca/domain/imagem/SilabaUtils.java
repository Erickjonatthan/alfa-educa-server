package com.projeto.alfaeduca.domain.imagem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SilabaUtils {

    private static final String ACENTO = "[áéíóúâêôãõüöäëï]";
    private static final String VOGAL = "[áéíóúâêôãõàèaeiouüöäëï]";
    private static final String CONSOANTE = "[bcçdfghjklmñnpqrstvwyxz]";

    private static final Map<Integer, String> SYL = new HashMap<>();
    private static final Map<Character, Integer> SPRI = new HashMap<>();

    static {
        SYL.put(20, " -.!?:;");
        SYL.put(10, "bçdfgjkpqtv");
        SYL.put(8, "sc");
        SYL.put(7, "m");
        SYL.put(6, "lzx");
        SYL.put(5, "nr");
        SYL.put(4, "h");
        SYL.put(3, "wy");
        SYL.put(2, "eaoáéíóúôâêûàãõäëïöü");
        SYL.put(1, "iu");

        for (Map.Entry<Integer, String> entry : SYL.entrySet()) {
            for (char c : entry.getValue().toCharArray()) {
                SPRI.put(c, entry.getKey());
            }
        }
    }

    private static final String SYL_SEP_PAIR = Pattern.compile("(\\p{L})(\\p{L})")
            .matcher(SYL.get(20))
            .replaceAll("(?<=($1))(?=($2))");

    public static List<String> separarSilabas(String palavra) {
        return separarSilabas(palavra, "|");
    }

    public static List<String> separarSilabas(String palavra, String sylSep) {
        if (sylSep == null) sylSep = "|";

        Map<Integer, Character> punctuation = new HashMap<>();

        Matcher punctuationMatcher = Pattern.compile("(\\p{P})").matcher(palavra);
        while (punctuationMatcher.find()) {
            punctuation.put(punctuationMatcher.start(), punctuationMatcher.group().charAt(0));
        }
        palavra = punctuationMatcher.replaceAll("");

        palavra = palavra.replaceAll(SYL_SEP_PAIR, "|");

        Pattern pattern = Pattern.compile("(\\p{L})(?=(\\p{L})(\\p{L}))");
        Matcher matcher = pattern.matcher(palavra);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            char m1 = matcher.group(1).toLowerCase().charAt(0);
            char m2 = matcher.group(2).toLowerCase().charAt(0);
            char m3 = matcher.group(3).toLowerCase().charAt(0);
            String replacement = SPRI.get(m1) < SPRI.get(m2) && SPRI.get(m2) >= SPRI.get(m3) ? matcher.group(1) + "|" : matcher.group(1);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        palavra = sb.toString()
                .replaceAll("(" + VOGAL + ")(" + CONSOANTE + ")(" + VOGAL + ")", "$1|$2$3")
                .replaceAll("(de)(us)", "$1|$2")
                .replaceAll("([a])(i[ru])$", "$1|$2")
                .replaceAll("(?<!^h)([ioeê])([e])", "$1|$2")
                .replaceAll("([ioeêé])([ao])", "$1|$2")
                .replaceAll("([^qg]u)(ai|ou|a)", "$1|$2")
                .replaceAll("([^qgc]u)(i|ei|iu|ir|" + ACENTO + "|e)", "$1|$2")
                .replaceAll("([lpt]u)\\|(i)(?=\\|[ao])", "$1$2")
                .replaceAll("([^q]u)(o)", "$1|$2")
                .replaceAll("([aeio])(" + ACENTO + ")", "$1|$2")
                .replaceAll("([íúô])(" + VOGAL + ")", "$1|$2")
                .replaceAll("^a(o|e)", "a|$1")
                .replaceAll("rein", "re|in")
                .replaceAll("ae", "a|e")
                .replaceAll("ain", "a|in")
                .replaceAll("ao(?!s)", "a|o")
                .replaceAll("cue", "cu|e")
                .replaceAll("cui(?=\\|[mnr])", "cu|i")
                .replaceAll("cui(?=\\|da\\|de$)", "cu|i")
                .replaceAll("coi(?=[mn])", "co|i")
                .replaceAll("cai(?=\\|?[mnd])", "ca|i")
                .replaceAll("ca\\|i(?=\\|?[m]" + ACENTO + ")", "cai")
                .replaceAll("cu([áó])", "cu|$1")
                .replaceAll("ai(?=\\|?[z])", "a|i")
                .replaceAll("i(u\\|?)n", "i|$1n")
                .replaceAll("i(u\\|?)r", "i|$1r")
                .replaceAll("i(u\\|?)v", "i|$1v")
                .replaceAll("i(u\\|?)l", "i|$1l")
                .replaceAll("ium", "i|um")
                .replaceAll("([ta])iu", "$1i|u")
                .replaceAll("miu\\|d", "mi|u|d")
                .replaceAll("au\\|to(?=i)", "au|to|")
                .replaceAll("(?<=" + VOGAL + ")i\\|nh(?=[ao])", "|i|nh")
                .replaceAll("oi([mn])", "o|i$1")
                .replaceAll("oi\\|b", "o|i|b")
                .replaceAll("ois(?!$)", "o|is")
                .replaceAll("o(i\\|?)s(?=" + ACENTO + ")", "o|$1s")
                .replaceAll("([dtm])aoi", "$1a|o|i")
                .replaceAll("(?<=[trm])u\\|i(?=\\|?[tvb][oa])", "ui")
                .replaceAll("^gas\\|tro(?!-)", "gas|tro|")
                .replaceAll("^fais", "fa|is")
                .replaceAll("^hie", "hi|e")
                .replaceAll("^ciu", "ci|u")
                .replaceAll("(?<=^al\\|ca)\\|i", "i")
                .replaceAll("(?<=^an\\|ti)(p)\\|?", "|$1")
                .replaceAll("(?<=^an\\|ti)(\\-p)\\|?", "$1")
                .replaceAll("(?<=^neu\\|ro)p\\|", "|p")
                .replaceAll("(?<=^pa\\|ra)p\\|", "|p")
                .replaceAll("(?<=^ne\\|)op\\|", "o|p")
                .replaceAll("^re(?=[i]\\|?[md])", "re|")
                .replaceAll("^re(?=i\\|n[ií]\\|c)", "re|")
                .replaceAll("^re(?=i\\|nau\\|g)", "re|")
                .replaceAll("^re(?=[u]\\|?[ntsr])", "re|")
                .replaceAll("^vi\\|de\\|o(" + VOGAL + ")", "o|$1")
                .replaceAll("s\\|s$", "ss")
                .replaceAll("\\|\\|", "\\|");

        List<String> silabas = new ArrayList<>();
        for (String s : palavra.split("\\|")) {
            silabas.add(s);
        }
        return silabas;
    }
}