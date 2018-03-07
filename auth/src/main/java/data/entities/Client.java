package data.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
public class Client {

    private ClientType type = ClientType.Confidential;
    @Id
    @GeneratedValue
    private UUID id;
    @GeneratedValue
    private UUID secret;
    private String name;
    @OneToMany(mappedBy = "client", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<RedirectUri> redirectUris = new HashSet<>();
    @OneToMany(mappedBy = "client", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<AuthorizationCode> authorizationCodes = new HashSet<>();

    enum ClientType {Confidential, Public}

}
