package pt.up.fe.pe25.task.notification.plugins.whatsapp;

import javax.inject.Inject;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.Config;

@ApplicationScoped
public class WhatsAppProperties {

    @Inject
    Config config;

    public String get_PRODUCT_ID() {
        return config.getValue("pt.fe.up.pe25.whatsapp.product_id", String.class);
    }

    public String get_PHONE_ID() {
        return config.getValue("pt.fe.up.pe25.whatsapp.phone_id", String.class);
    }

    public String get_MAYTAPI_KEY() {
        return config.getValue("pt.fe.up.pe25.whatsapp.maytapi_key", String.class);
    }

}
