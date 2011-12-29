package events;

import data.Player;
import bot.IdleBot;

public class Event {
	
	public static final int EVENT_TIME = 15000;
	
	public Event() {
		Player p = IdleBot.botref.getRandomPlayer();
		
		int i = (int) (Math.random() * 100);
		if(i == 1) {
			new Cataclysm();
		} else if(i<21) {
			new MoneyEvent(p);
		} else if(i<54) {
			new ItemEvent(p);
		} else if(i<86){
			new TimeEvent(p);
		} 
	}
}
