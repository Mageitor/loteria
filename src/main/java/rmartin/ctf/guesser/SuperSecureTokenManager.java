package rmartin.ctf.guesser;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class SuperSecureTokenManager {

    private static final Logger log = Logger.getLogger(SuperSecureTokenManager.class.getName());

    private static final int MAX_NUMBER = 1_234_000_100;

    /**
     * Each client has its own state, CTF participants should be isolated
     */
    Map<String, ClientTokenState> clients = new ConcurrentHashMap<>();

    public Iterable<Integer> getUsedTokens(HttpSession session){
        String id = session.getId();
        var usedTokens = clients.get(id).usedTokens;
        log.info(String.format("Returning used tokens for client %s --> %s", id, usedTokens));
        return usedTokens;
    }

    public boolean isValidToken(HttpSession session, int token){
        String id = session.getId();
        clients.computeIfAbsent(id, ClientTokenState::new);
        boolean result = clients.get(id).isValidToken(token);
        log.info(String.format("Validating token for client %s got %s --> %s", id, token, result));
        return result;
    }

    public void resetState(HttpSession session){
        String id = session.getId();
        log.info(String.format("Resetting state for client %s", id));
        clients.put(id, new ClientTokenState(id));
    }

    private static class ClientTokenState {
        final String sessionId;
        final Queue<Integer> usedTokens;
        // Each token is a random number in range [0, 1_000_000_000)
        int currentToken;
        Random random;

        ClientTokenState(String sessionId) {
            this.sessionId = sessionId;
            this.usedTokens = new ConcurrentLinkedQueue<>();
            this.random = new Random(System.currentTimeMillis());
            this.currentToken = random.nextInt(MAX_NUMBER);
        }

        boolean isValidToken(int token){
            boolean check = token == currentToken;
            usedTokens.add(currentToken);
            currentToken = random.nextInt(MAX_NUMBER);
            return check;
        }
    }
}
