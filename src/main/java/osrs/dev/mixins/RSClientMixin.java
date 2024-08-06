package osrs.dev.mixins;

import osrs.dev.annotations.Inject;
import osrs.dev.annotations.MethodHook;
import osrs.dev.annotations.Mixin;
import osrs.dev.annotations.Shadow;
import osrs.dev.api.RSClient;
import osrs.dev.util.Logger;
import osrs.dev.util.eventbus.events.MenuOptionClicked;

import java.util.UUID;

@Mixin("Client")
public abstract class RSClientMixin implements RSClient
{
    @Inject
    private String uid;

    @Inject
    @Override
    public String getClientID()
    {
        if(uid == null)
        {
            uid = UUID.randomUUID().toString();
        }
        return uid;
    }

    @Shadow("clientField")
    @Override
    public abstract RSClient getClient();

    @Shadow(value = "menuAction", method = true)
    @Override
    public void sendMenuAction(int param0, int param1, int opcode, int identifier, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY) {}

    @Inject
    @Override
    public void sendMenuAction(MenuOptionClicked event) {
        System.out.println("Sending lol");
        sendMenuAction(event.getParam0(), event.getParam1(), event.getOpcode(), event.getIdentifier(),
                event.getItemId(), event.getWorldViewId(), event.getOption(), event.getTarget(),
                event.getCanvasX(), event.getCanvasY());
    }
}
