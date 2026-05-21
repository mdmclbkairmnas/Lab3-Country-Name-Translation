package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, Map<String, String>> countryMap = new HashMap<>();
    private final List<String> countryCodes = new ArrayList<>();

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject countryObj = jsonArray.getJSONObject(i);
                String countryCode = countryObj.getString("alpha3").toLowerCase();
                countryCodes.add(countryCode);
                Map<String, String> translations = new HashMap<>();
                for (String key : countryObj.keySet()) {
                    if ("id".equals(key) || "alpha2".equals(key) || "alpha3".equals(key)) {
                        continue;
                    }
                    translations.put(key, countryObj.getString(key));
                }
                countryMap.put(countryCode, translations);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        if (countryMap.get(country.toLowerCase()) == null) {
            return new ArrayList<>();
        }
        else {
            return new ArrayList<>(countryMap.get(country.toLowerCase()).keySet());
        }
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(this.countryCodes);
    }

    @Override
    public String translate(String country, String language) {
        if (countryMap.get(country.toLowerCase()) == null) {
            return null;
        }
        else {
            return countryMap.get(country.toLowerCase()).get(language.toLowerCase());
        }
    }
}
