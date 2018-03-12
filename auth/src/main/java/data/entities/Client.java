package data.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Client {

    private ClientType type = ClientType.Confidential;

    @Id
    private String id = UUID.randomUUID().toString();

    private String secret = UUID.randomUUID().toString();
    private String name;

    @OneToMany(mappedBy = "client", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<RedirectUri> redirectUris = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<AuthorizationCode> authorizationCodes = new HashSet<>();

    enum ClientType {Confidential, Public}

}
