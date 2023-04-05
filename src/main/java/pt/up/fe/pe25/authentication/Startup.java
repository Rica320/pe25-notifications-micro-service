package pt.up.fe.pe25.authentication;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;

import java.util.Collections;
import java.util.List;


@Singleton
public class Startup {
    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        User.deleteAll();
        User.add("admin", "admin", Collections.singletonList("admin"));
        User.add("user", "user", List.of("user", "admin"));
    }
}
