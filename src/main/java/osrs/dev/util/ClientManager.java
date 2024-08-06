package osrs.dev.util;

import jdk.jfr.Event;
import lombok.Getter;
import lombok.Setter;
import osrs.dev.annotations.Subscribe;
import osrs.dev.api.RSClient;
import osrs.dev.util.eventbus.EventBus;
import osrs.dev.util.eventbus.clientManagerEvents.Multibox;
import osrs.dev.util.eventbus.events.MenuOptionClicked;

import java.util.ArrayList;
import java.util.List;

public class ClientManager {
    @Getter
    private static final ClientManager INSTANCE = new ClientManager();

    @Getter
    private static final List<RSClient> clients = new ArrayList<>();

    @Getter
    private static String currentClient = null;

    @Getter
    @Setter
    private static String multiboxID;

    static {
        EventBus.register(INSTANCE);
    }

    public static void setCurrentClient(String clientId) {
        currentClient = clientId;

        for (RSClient client : clients) {
            client.setDisableRender(!client.getClientID().equals(currentClient));
        }
    }

    public static RSClient getClient() {
        return getClient(currentClient);
    }

    public static RSClient getClient(String clientId) {
        if (clientId == null)
            return null;

        return clients.stream().filter(c -> c.getClientID().equals(clientId)).findFirst().orElse(null);
    }

    @Subscribe
    public void onMultibox(RSClient client, Multibox event) {
        Logger.info("Hiitting?");
        MenuOptionClicked menuSelection = event.getMenuOption();
        clients.stream().filter(c -> c != client).forEach(c -> c.sendMenuAction(menuSelection));
    }
}
