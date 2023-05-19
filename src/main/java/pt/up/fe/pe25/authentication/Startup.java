package pt.up.fe.pe25.authentication;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;

import java.util.Collections;
import java.util.List;


@Singleton
public class Startup {
    /**
     * Loads the users into the database, for development purposes. Not recommended for production.
     * @param evt the startup event
     */
    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        User.deleteAll();
        User.add("admin", "admin", List.of("user", "admin"));
        User.add("user", "user", Collections.singletonList("user"));
    }
}
