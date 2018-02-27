package token;

import data.AbstractDAO;
import data.entities.AccessToken;
import data.entities.RefreshToken;

public class TokenDAO extends AbstractDAO {
    public AccessToken persist(AccessToken token) {
        return persistOne(token);
    }

    public RefreshToken persist(RefreshToken token) {
        return persistOne(token);
    }
}
