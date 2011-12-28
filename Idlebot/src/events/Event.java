package events;

import data.Player;
import bot.IdleBot;

public class Event {
	
	public static final int EVENT_TIME = 15000;
	
	public Event() {
		Player p = IdleBot.botref.getRandomPlayer();
		
		// for testing new MoneyEvent(p);
	}
}
