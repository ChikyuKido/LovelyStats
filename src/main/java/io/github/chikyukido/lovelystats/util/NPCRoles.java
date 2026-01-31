package io.github.chikyukido.lovelystats.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

public class NPCRoles {
    private static Map<String, String> ROLES = Map.of();
    public static void init() throws IOException {
        Gson gson = new Gson();
        try (InputStream is = NPCRoles.class.getResourceAsStream("/roles.json");
             InputStreamReader reader = new InputStreamReader(is)) {

            Type mapType = new TypeToken<Map<String, String>>() {}.getType();
            ROLES = gson.fromJson(reader, mapType);
        }
    }
    public static String getRole(String id) {
        String role = ROLES.get(id);
        if(role == null || role.equals("NONE")) {
            role = id;
        }
        return role;
    }
}
