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
import java.util.ArrayList;
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

        HashMap<String, String> tweets = new HashMap<>();
        HashMap<String, Integer> user_rank = new HashMap<>();

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
//                    if (tweets.containsKey(id)) {
//                        System.out.println("No map: " + tweets.get(id));
//                        System.out.println("\nDuplicado: " + line);
//                        System.out.println("\n\n\n\n");
//                    }
                    tweets.put(id, line);

                    String id_user = actualObj.get("user").get("id_str").asText();

                    int followers_count = actualObj.get("user").get("followers_count").asInt();
                    user_rank.put(id_user, followers_count);
                    line = reader.readLine();
                    lines++;
                }
            }
        }

        System.out.println("Total Tweets Lidos: " + lines);
        System.out.println("Total Tweets no Map: " + tweets.size());
        System.out.println("Uusários Computados: " + user_rank.size());

        Map<String, Integer> ordered = sortByValue(user_rank, false);
        HashMap<String, List<String>> top_messages = new HashMap<>();

        int top = ordered.size();
        for (Entry<String, Integer> o : ordered.entrySet()) {
            if (top > 0) {
                String c_id = o.getKey();
                System.out.println("Atual: " + top);
                for (Entry<String, String> tweet : tweets.entrySet()) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(tweet.getValue());

                    String t_user = actualObj.get("user").get("id_str").asText();
                    String text = actualObj.get("text").asText();

                    if (t_user.equals(c_id)) {
                        if (top_messages.containsKey(c_id)) {
                            List<String> temp = top_messages.get(c_id);
                            temp.add(text);
                            top_messages.put(c_id, temp);

                        } else {
                            List<String> messages = new ArrayList<>();
                            messages.add(text);
                            top_messages.put(c_id, messages);
                        }
                    }
                }

            } else {
                break;
            }

            top--;
        }

        int totalM = 0;
        for (Entry<String, List<String>> tr : top_messages.entrySet()) {
            totalM += tr.getValue().size();
            //System.out.println(tr.getKey() + ": " + tr.getValue().size());
        }

        System.out.println("Total de Mensagens dos usuários: " + totalM);

//
//        for (Entry<String, String> t : tweets.entrySet()) {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode actualObj = mapper.readTree(t.getValue());
//
//            String id_user = actualObj.get("user").get("id_str").asText();
//            if (id_user.equals("14594813")) {
//                String text = actualObj.get("text").asText();
//                System.out.println(text);
//            }
//        }
//        HashMap<String, Tweet> map_tweets = new HashMap<>();
//        int countFile = 1;
//
//        FileWriter out = new FileWriter(new File("planilha" + countFile + ".csv"));
//        BufferedWriter writer = new BufferedWriter(out);
//        //writer.write("localização;texto;qtd_emojis\n");
//
//        File folder = new File("C:\\Users\\eduardo\\Documents\\GitHub\\TCC-JsonParser\\exp\\agosto\\");
//        File[] listOfFiles = folder.listFiles();
//        int lines = 1;
//
//        for (File file : listOfFiles) {
//            if (file.isFile()) {
//                FileReader in = new FileReader(file);
//                BufferedReader reader = new BufferedReader(in);
//                String line = reader.readLine();
//
//                while (line != null) {
//                    ObjectMapper mapper = new ObjectMapper();
//                    JsonNode actualObj = mapper.readTree(line);
//
//                    String id = actualObj.get("id").asText();
//                    String location = actualObj.get("user").get("location").asText();
//                    String text = actualObj.get("text").asText();
//
//                    if (text.contains("RT")) {
//                        text = text.substring(0, text.indexOf("RT"));
//                    }
//
//                    if (!text.equals("")) {
//                        EmojiFrequency ef = new EmojiFrequency();
//                        ef.updateFrequencies(text);
//
//                        Tweet t = new Tweet(location, id);
//                        map_tweets.put(StringUtils.normalizeSpace(text), t);
//
//                        String txtDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(text));
//
//                        StringBuilder emojis = new StringBuilder();
//
//                        ef.getFrequencies().forEach((key, value) -> emojis.append(key.getAliases().get(0)).append("   "));
//                        String toWrite = location + ";\"" + txtDecoded + "\";\"" + ef.getFrequencies().size() + " -> " + emojis.toString() + "\"";
//                        System.out.println(txtDecoded);//writer.write(toWrite + "\n");
//                    }
//
//                    line = reader.readLine();
//                    lines++;
//                }
//            }
//        }
//
//        //contador para tweets com localização
//        int count_wl = 0;
//
////        for (Entry<String, Tweet> ti : map_tweets.entrySet()) {
////            if (!ti.getValue().getLocation().equals("")) {
////                count_wl++;
////                String toWrite = "{\"id\": \"" + ti.getValue().getId() + "\", \"texto\": \"" + ti.getKey() + "\", \"anotadores\": []}";
////                writer.write(toWrite + "\n");
////            }
////            //System.out.println(ti.getValue().getId() + " : " + ti.getValue().getLocation());
////        }
////      
//        int countWrote = 1;
//        writer.write("id;texto;remover \n");
//        for (Entry<String, Tweet> ti : map_tweets.entrySet()) {
//            if (countWrote == (map_tweets.size() / 4) + 2) {
//                writer.close();
//                countFile++;
//                out = new FileWriter(new File("planilha" + countFile + ".csv"));
//                writer = new BufferedWriter(out);
//                writer.write("id;texto;remover \n");
//                countWrote = 1;
//            }
//
//            String toWrite = ti.getValue().getId() + "; \"" + ti.getKey() + "\"; ";
//            writer.write(toWrite + "\n");
//            countWrote++;
//
//            //System.out.println(ti.getValue().getId() + " : " + ti.getValue().getLocation());
//        }
//
//        writer.close();
//
//        System.out.println("Total: " + lines + " Sem Repetidos: " + map_tweets.size());
//        System.out.println("Porcentagem com localizaçao: " + (count_wl * 100) / map_tweets.size() + "%");
//
////        HashMap<String, Integer> ids_freq = new HashMap<>();
////        EmojiFrequency ef = new EmojiFrequency();
////
////        FileWriter out = new FileWriter(new File("ht.csv"));
////        BufferedWriter writer = new BufferedWriter(out);
////
////        FileReader in = new FileReader(new File("busca_hashtag.json"));
////        BufferedReader reader = new BufferedReader(in);
////        String line = reader.readLine();
////        int processados = 0;
////
////        writer.write("id;lang;criado;texto;retweet;menção\n");
////
////        while (line != null) {
////            ObjectMapper mapper = new ObjectMapper();
////            JsonNode actualObj = mapper.readTree(line);
////
////            String criadoEm = actualObj.get("created_at").asText();
////            String id = actualObj.get("id").asText();
////            String lang = actualObj.get("lang").asText();
////
////            String text = actualObj.get("text").asText();
////            if (text.contains("RT")) {
////                text = text.substring(0, text.indexOf("RT"));
////            }
////
////            String rt_text = "";
////            if (actualObj.has("rt_text")) {
////                rt_text = actualObj.get("rt_text").asText();
////            }
////
////            String qt_text = "";
////            if (actualObj.has("qt_text")) {
////                qt_text = actualObj.get("qt_text").asText();
////            }
////
////            ef.updateFrequencies(text);
////
////            String txtDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(text));
////            String qtTextDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(qt_text));
////            String rtTextDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(rt_text));
////
////            String toWrite = id + ";" + lang + ";\"" + criadoEm + "\";\"" + txtDecoded + "\";\"" + rtTextDecoded + "\";\"" + qtTextDecoded + "\"";
////            writer.write(toWrite + "\n");
////
////            String id_user = actualObj.get("user").get("id_str").asText();
////            String location = actualObj.get("user").get("location").asText();
////
////            System.out.println("Location: " + location);
////            if (ids_freq.containsKey(id_user)) {
////                ids_freq.put(id_user, (ids_freq.get(id_user) + 1));
////            } else {
////                ids_freq.put(id_user, 1);
////            }
////
////            //System.out.println(id);
////            //Incremento e leitura de nova linha;
////            processados++;
////            line = reader.readLine();
////        }
////
////        //Todos os emojis encontrados na coleta
////        //System.out.println("EMOJIS ENCONTRADOS NOS TWEETS DA COLETA: ");
////        for (Map.Entry<Emoji, Integer> entry : (ef.getFrequencies()).entrySet()) {
////            //System.out.println(entry.getKey().getAliases().get(0) + " | " + entry.getValue());
////        }
////
////        System.out.println("\n\nFREQUENCIA DE IDS: ");
////        
////        printMap(sortByValue(ids_freq, false));
////        
////        writer.close();
////        reader.close();
////        System.out.println(processados);
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

    private static void printMapI(Map<String, Integer> map) {
        map.forEach((key, value) -> System.out.println(key + " => " + value));
    }

    private static void printMapS(Map<String, String> map) {
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
