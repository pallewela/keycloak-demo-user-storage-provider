package org.pallewela.demo.keycloak;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.HashMap;
import java.util.Map;


public class DemoStorageProviderFactory implements UserStorageProviderFactory<DemoUserStorageProvider> {

    private static Map<String, String> USERS = new HashMap<>();
    static {
        USERS.put("aswilk", "asitha");
        USERS.put("bjokse", "bjorn");
        USERS.put("mjaylk", "madusha");
    }

    @Override
    public String getId() {
        return "demo-user-storage-provider";
    }

    @Override
    public DemoUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new DemoUserStorageProvider(session, model, USERS);
    }

}
