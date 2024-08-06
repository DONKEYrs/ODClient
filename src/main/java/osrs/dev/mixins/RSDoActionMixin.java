package osrs.dev.mixins;

import osrs.dev.annotations.Inject;
import osrs.dev.annotations.MethodHook;
import osrs.dev.annotations.Mixin;
import osrs.dev.annotations.Shadow;
import osrs.dev.api.RSClient;
import osrs.dev.util.ClientManager;
import osrs.dev.util.Logger;
import osrs.dev.util.eventbus.EventBus;
import osrs.dev.util.eventbus.clientManagerEvents.Multibox;
import osrs.dev.util.eventbus.events.MenuOptionClicked;

import java.util.UUID;

@Mixin("menuAction")
public abstract class RSDoActionMixin implements RSClient
{
    @Inject
    private static String multiboxID;

    @Shadow("clientField")
    public static RSClient getStaticClient()
    {
        return null;
    }

    @MethodHook("menuAction")
    public static boolean doAction(int param0, int param1, int opcode, int identifier, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY) {
        MenuOptionClicked event = new MenuOptionClicked(param0, param1, opcode, identifier, itemId, worldViewId, option, target, canvasX, canvasY);
        if (getStaticClient().getClientID().equals(ClientManager.getMultiboxID())) {
            System.out.println("POSTED MULTIBOX");
            EventBus.post(getStaticClient(), new Multibox(event));
        }
        System.out.println(ClientManager.getMultiboxID());
        EventBus.post(getStaticClient(), event);
        return false;
    }
}
