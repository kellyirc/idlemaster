package events;

import java.util.Random;

import data.Playable;
import data.Player;
import bot.IdleBot;

public class Event {
	
	public static final int EVENT_TIME = 5000;
	public static final int EVENT_MAX_PROB = 155;
	public static final int EVENT_BASE_PROB = 1000;
	
	public Event() {
		this(IdleBot.botref.getRandomPlayer(), EVENT_BASE_PROB);
	}
	
	public Event(int maxProb) {
		this(IdleBot.botref.getRandomPlayer(), maxProb);
	}
	
	public Event(Player p, int maxProb) {
		
		int i = (int) (Math.random() * maxProb);
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

	
	public static String replaceGender(String string, Playable player) {
		if(player.getIsMale() == null) {
			string = string.replaceAll("%hisher", "his/her").replaceAll("%himher", "him/her").replaceAll("%she", "s/he").replaceAll("%hishers", "his/hers");
		} else if(player.getIsMale() == true) {
			string = string.replaceAll("%hisher", "his").replaceAll("%himher", "him").replaceAll("%she", "he").replaceAll("%hishers", "his");
		} else {
			string = string.replaceAll("%hisher", "her").replaceAll("%himher", "her").replaceAll("%she", "she").replaceAll("%hishers", "hers");
		}
		return string;
	}
	
}
