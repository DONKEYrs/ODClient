package osrs.dev.util.eventbus.clientManagerEvents;

import lombok.Data;
import osrs.dev.util.eventbus.events.MenuOptionClicked;

@Data
public class Multibox {
    private final MenuOptionClicked menuOption;
}
