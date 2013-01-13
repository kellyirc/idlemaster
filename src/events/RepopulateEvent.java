package events;

import bot.IdleBot;

public class RepopulateEvent {

	public RepopulateEvent() {
		IdleBot.botref.messageChannel("All of the monsters in the world simultaneously died and respawned anew!");
		IdleBot.botref.repop();
	}
	
}
