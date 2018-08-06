/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eduardo.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class Parser {

    public static void main(String[] args) throws IOException {

        HashMap<String, Tweet> map_tweets = new HashMap<>();

        FileWriter out = new FileWriter(new File("exp.csv"));
        BufferedWriter writer = new BufferedWriter(out);
        writer.write("localização;texto;qtd_emojis\n");

        File folder = new File("C:\\Users\\eduardo\\Documents\\GitHub\\TCC-JsonParser\\exp\\julho\\");
        File[] listOfFiles = folder.listFiles();
        int lines = 1;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                FileReader in = new FileReader(file);
                BufferedReader reader = new BufferedReader(in);
                String line = reader.readLine();

                while (line != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(line);

                    String id = actualObj.get("id").asText();
                    String location = actualObj.get("user").get("location").asText();
                    String text = actualObj.get("text").asText();

                    if (text.contains("RT")) {
                        text = text.substring(0, text.indexOf("RT"));
                    }

                    if (!text.equals("")) {
                        EmojiFrequency ef = new EmojiFrequency();
                        ef.updateFrequencies(text);

                        Tweet t = new Tweet(location, id);
                        map_tweets.put(StringUtils.normalizeSpace(text), t);

                        String txtDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(text));

                        StringBuilder emojis = new StringBuilder();

                        ef.getFrequencies().forEach((key, value) -> emojis.append(key.getAliases().get(0)).append("   "));
                        String toWrite = location + ";\"" + txtDecoded + "\";\"" + ef.getFrequencies().size() + " -> " + emojis.toString() + "\"";
                        writer.write(toWrite + "\n");
                    }

                    line = reader.readLine();
                    lines++;
                }
            }
        }

        //contador para tweets com localização
        int count_wl = 0;

        for (Entry<String, Tweet> ti : map_tweets.entrySet()) {
            if (!ti.getValue().getLocation().equals("")) {
                count_wl++;
            }
            //System.out.println(ti.getValue().getId() + " : " + ti.getValue().getLocation());
        }

        System.out.println("Total: " + lines + " Sem Repetidos: " + map_tweets.size());
        System.out.println("Porcentagem com localizaçao: " + (count_wl * 100) / map_tweets.size() + "%");

//        HashMap<String, Integer> ids_freq = new HashMap<>();
//        EmojiFrequency ef = new EmojiFrequency();
//
//        FileWriter out = new FileWriter(new File("ht.csv"));
//        BufferedWriter writer = new BufferedWriter(out);
//
//        FileReader in = new FileReader(new File("busca_hashtag.json"));
//        BufferedReader reader = new BufferedReader(in);
//        String line = reader.readLine();
//        int processados = 0;
//
//        writer.write("id;lang;criado;texto;retweet;menção\n");
//
//        while (line != null) {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode actualObj = mapper.readTree(line);
//
//            String criadoEm = actualObj.get("created_at").asText();
//            String id = actualObj.get("id").asText();
//            String lang = actualObj.get("lang").asText();
//
//            String text = actualObj.get("text").asText();
//            if (text.contains("RT")) {
//                text = text.substring(0, text.indexOf("RT"));
//            }
//
//            String rt_text = "";
//            if (actualObj.has("rt_text")) {
//                rt_text = actualObj.get("rt_text").asText();
//            }
//
//            String qt_text = "";
//            if (actualObj.has("qt_text")) {
//                qt_text = actualObj.get("qt_text").asText();
//            }
//
//            ef.updateFrequencies(text);
//
//            String txtDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(text));
//            String qtTextDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(qt_text));
//            String rtTextDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(rt_text));
//
//            String toWrite = id + ";" + lang + ";\"" + criadoEm + "\";\"" + txtDecoded + "\";\"" + rtTextDecoded + "\";\"" + qtTextDecoded + "\"";
//            writer.write(toWrite + "\n");
//
//            String id_user = actualObj.get("user").get("id_str").asText();
//            String location = actualObj.get("user").get("location").asText();
//
//            System.out.println("Location: " + location);
//            if (ids_freq.containsKey(id_user)) {
//                ids_freq.put(id_user, (ids_freq.get(id_user) + 1));
//            } else {
//                ids_freq.put(id_user, 1);
//            }
//
//            //System.out.println(id);
//            //Incremento e leitura de nova linha;
//            processados++;
//            line = reader.readLine();
//        }
//
//        //Todos os emojis encontrados na coleta
//        //System.out.println("EMOJIS ENCONTRADOS NOS TWEETS DA COLETA: ");
//        for (Map.Entry<Emoji, Integer> entry : (ef.getFrequencies()).entrySet()) {
//            //System.out.println(entry.getKey().getAliases().get(0) + " | " + entry.getValue());
//        }
//
//        System.out.println("\n\nFREQUENCIA DE IDS: ");
//        
//        printMap(sortByValue(ids_freq, false));
//        
//        writer.close();
//        reader.close();
//        System.out.println(processados);
    }

    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean order) {
        List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                        ? o2.getKey().compareTo(o1.getKey())
                        : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    private static void printMap(Map<String, Integer> map) {
        map.forEach((key, value) -> System.out.println(key + " => " + value));
    }

    public static class EmojiFrequency extends EmojiParser {

        private Map<Emoji, Integer> frequencies = new HashMap<Emoji, Integer>();

        public EmojiFrequency() {
            super();
        }

        public void updateFrequencies(String text) {
            for (UnicodeCandidate uc : getUnicodeCandidates(text)) {
                frequencies.put(uc.getEmoji(), frequencies.getOrDefault(uc.getEmoji(), 0) + 1);
            }
        }

        public Map<Emoji, Integer> getFrequencies() {
            return frequencies;
        }
    }

}
