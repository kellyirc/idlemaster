package events;

import java.util.Random;

import data.Player;
import bot.IdleBot;

public class Event {
	
	public static final int EVENT_TIME = 15000;
	
	public Event() {
		Player p = IdleBot.botref.getRandomPlayer();
		
		int i = (int) (Math.random() * 250);
		Random r = new Random();
		if(i == 1) {
			new Cataclysm();
		} else if(i<71) {
			new MoneyEvent(p, r.nextBoolean());
		} else if(i<144) {
			new ItemEvent(p, r.nextBoolean());
		} else if(i<216){
			new TimeEvent(p);
		}  else {
			new TeamEvent();
		}
	}
	
}
