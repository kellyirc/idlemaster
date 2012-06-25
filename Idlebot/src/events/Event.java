package events;

import java.util.Random;

import data.Player;
import bot.IdleBot;

public class Event {
	
	public static final int EVENT_TIME = 5000;
	
	public Event() {
		this(IdleBot.botref.getRandomPlayer());
	}
	
	public Event(Player p) {
		
		int i = (int) (Math.random() * 1000);
		Random r = new Random();
		
		if(i == 1) {
			new Cataclysm();
			
		} else if(i < 40) {
			new MoneyEvent(p, r.nextBoolean());
			
		} else if(i < 90) {
			new ItemEvent(p, r.nextBoolean());
			
		} else if(i < 150) {
			new TimeEvent(p);
			
		} else if(i < 155) {
			new ItemFindEvent(p);
			
		} else if(i < 160) {
			//TODO These are broken for battles
			//new TeamEvent();
		}
	}
	
}
