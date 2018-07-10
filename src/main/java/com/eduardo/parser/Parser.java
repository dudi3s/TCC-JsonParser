/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eduardo.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.lang.StringEscapeUtils;

public class Parser {

    public static void main(String[] args) throws IOException {

        FileReader file = new FileReader(new File("busca_hashtag.json"));
        BufferedReader br = new BufferedReader(file);
        String line = br.readLine();
        int c = 0;

        while (line != null) {
            ObjectMapper mapper = new ObjectMapper();
            line = line.substring(0, line.length() - 1);
            JsonNode actualObj = mapper.readTree(line);

            String criadoEm = actualObj.get("created_at").asText();

            String text = actualObj.get("text").asText();
            String txtDecoded = StringEscapeUtils.unescapeJava(text);

            String id = actualObj.get("id").asText();
            String timezone = actualObj.get("user").get("time_zone").asText();
            String lang = actualObj.get("lang").asText();

            if (!text.startsWith("RT")) {
                System.out.println(id + " " + lang + " " + criadoEm + " " + timezone + " ");
            }

            //Incremento e leitura de nova linha;
            c++;
            line = br.readLine();
        }

        System.out.println(c);

    }
}
