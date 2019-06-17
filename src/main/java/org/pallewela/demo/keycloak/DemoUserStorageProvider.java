package org.pallewela.demo.keycloak;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DemoUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        UserQueryProvider,
        CredentialInputValidator {
    private Map<String, String> users;
    private KeycloakSession session;
    private ComponentModel model;
    private boolean used = false;

    public DemoUserStorageProvider(KeycloakSession session, ComponentModel model, Map<String, String> users) {
        this.session = session;
        this.model = model;
        this.users = users;
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        UserModel ret = null;
        if (users.containsKey(username)) {
            ret = new AbstractUserAdapter(session, realm, model) {
                @Override
                public String getUsername() {
                    return username;
                }
            };
        }
        used = true;
        return ret;
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        String password = users.get(user.getUsername());
        return credentialType.equals(CredentialModel.PASSWORD) && users.containsKey(user.getId());
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return credentialType.equals(CredentialModel.PASSWORD);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;

        UserCredentialModel cred = (UserCredentialModel) input;
        String password = users.get(user.getUsername());
        if (password == null) return false;
        return password.equals(cred.getValue());
    }


    @Override
    public void close() {
        System.out.println("close called");
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return users.size();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        List<UserModel> collect = users.keySet().stream().map(s -> new AbstractUserAdapter(session, realm, model) {
            @Override
            public String getUsername() {
                return s;
            }
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return users.keySet().stream().filter(s -> s.contains(search)).map(s -> new AbstractUserAdapter(session, realm, model) {
            @Override
            public String getUsername() {
                return s;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        List<UserModel> collect = users.keySet().stream().map(s -> new AbstractUserAdapter(session, realm, model) {
            @Override
            public String getUsername() {
                return s;
            }
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        throw new UnsupportedOperationException("not implemented yet");
    }

}
