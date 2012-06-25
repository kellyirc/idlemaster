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
		
		int i = (int) (Math.random() * 300);
		Random r = new Random();
		if(i == 1) {
			new Cataclysm();
		} else if(i<21) {
			new MoneyEvent(p, r.nextBoolean());
		} else if(i<31) {
			new ItemEvent(p, r.nextBoolean());
		} else if(i<51){
			new TimeEvent(p);
		}  else if(i < 71){
			//TODO These are broken for battles
			//new TeamEvent();
		} else if(i < 81){
			new ItemFindEvent(p);
		}
	}
	
}
