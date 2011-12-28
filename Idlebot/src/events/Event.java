package events;

import data.Player;
import bot.IdleBot;

public class Event {
	public Event() {
		Player p = IdleBot.botref.getRandomPlayer();
		
		new TimeEvent(p);
	}
}
