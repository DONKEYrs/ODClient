package osrs.dev.api;

import osrs.dev.util.eventbus.events.MenuOptionClicked;

public interface RSClient
{
    String getClientID();
    void setDisableRender(boolean bool);
    RSClient getClient();
    void setShouldExit(boolean bool);
    boolean getShouldExit();
    void sendMenuAction(int param0, int param1, int opcode, int identifier, int itemId, int worldViewId, String option, String target, int canvasX, int canvasY);
    void sendMenuAction(MenuOptionClicked e);
}
