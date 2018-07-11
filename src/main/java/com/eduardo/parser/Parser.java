/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eduardo.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class Parser {

    public static void main(String[] args) throws IOException {

        FileWriter out = new FileWriter(new File("ht.csv"));
        BufferedWriter writer = new BufferedWriter(out);

        FileReader in = new FileReader(new File("busca_hashtag.json"));
        BufferedReader reader = new BufferedReader(in);
        String line = reader.readLine();
        int processados = 0;

        writer.write("id;lang;criado;texto;retweet;menção\n");
        EmojiFrequency ef = new EmojiFrequency();

        while (line != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(line);

            String criadoEm = actualObj.get("created_at").asText();
            String id = actualObj.get("id").asText();
            String lang = actualObj.get("lang").asText();

            String text = actualObj.get("text").asText();
            if (text.contains("RT")) {
                text = text.substring(0, text.indexOf("RT"));
            }

            String rt_text = "";
            if (actualObj.has("rt_text")) {
                rt_text = actualObj.get("rt_text").asText();
            }

            String qt_text = "";
            if (actualObj.has("qt_text")) {
                qt_text = actualObj.get("qt_text").asText();
            }

            ef.updateFrequencies(text);

            String txtDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(text));
            String qtTextDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(qt_text));
            String rtTextDecoded = StringEscapeUtils.unescapeJava(StringUtils.normalizeSpace(rt_text));

            String toWrite = id + ";" + lang + ";\"" + criadoEm + "\";\"" + txtDecoded + "\";\"" + rtTextDecoded + "\";\"" + qtTextDecoded + "\"";
            writer.write(toWrite + "\n");

            //Incremento e leitura de nova linha;
            processados++;
            line = reader.readLine();
        }
        
        //Todos os emojis encontrados na coleta
        for (Map.Entry<Emoji, Integer> entry : (ef.getFrequencies()).entrySet()) {
            System.out.println(entry.getKey().getAliases().get(0) + " | " + entry.getValue());
        }
        
        writer.close();
        reader.close();
        System.out.println(processados);

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
