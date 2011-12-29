package events;

import data.Player;
import bot.IdleBot;

public class Event {
	
	public static final int EVENT_TIME = 15000;
	
	public Event() {
		Player p = IdleBot.botref.getRandomPlayer();
		
		int i = (int) (Math.random() * 250);
		if(i == 1) {
			new Cataclysm();
		} else if(i<71) {
			new MoneyEvent(p);
		} else if(i<144) {
			new ItemEvent(p);
		} else if(i<216){
			new TimeEvent(p);
		} 
	}
	
}
