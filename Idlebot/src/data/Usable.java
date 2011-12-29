package data;

import java.util.Random;

import events.*;

import bot.IdleBot;

public class Usable {
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	private String name;
	private int count=1;
	private Random r = new Random();
	
	public Usable(String name) {
		this.name = name;
	}
	
	public void addTo() {
		count++;
	}
	
	public boolean use(Player p) {
		if(count <= 0) return false;
		announce(announceMessage(name), p);
		switch(name) {
		case "fortunecookie":
			doFortuneCookie(p);
			break;
		}
		count--;
		return true;
	}
	
	private String announceMessage(String name) {
		switch(name) {
		case "fortunecookie":
			return "%player ate a fortune cookie...";
		default:
			return "THIS MESSAGE IS NOT CORRECT FOR "+name;
		}
	}
	
	private void doFortuneCookie(Player p) {
		switch(r.nextInt(13)) {
		case 1: case 2: case 3:
			IdleBot.botref.messageChannel("... and it tasted delicious!");
			new TimeEvent(p,TimeEvent.Type.Blessing);
			break;
		case 4: case 5: case 6:
			IdleBot.botref.messageChannel("... and it tasted delicious!");
			new ItemEvent(p,true);
			break;
		case 7: case 8: case 9:
			IdleBot.botref.messageChannel("... and it tasted delicious!");
			new MoneyEvent(p, true);
			break;
		case 10: case 11:
			IdleBot.botref.messageChannel("... but nothing happened!");
			break;
		case 12:
			IdleBot.botref.messageChannel("... but it was spoiled!");
			new MoneyEvent(p, false);
			break;
		case 13:
			IdleBot.botref.messageChannel("... but it was spoiled!");
			new ItemEvent(p, false);
			break;
		case 0:
			IdleBot.botref.messageChannel("... but it was spoiled!");
			new TimeEvent(p, TimeEvent.Type.Forsaken);
			break;
		}
	}

	private void announce(String s, Player p) {
		IdleBot.botref.messageChannel(s.replaceAll("%player", p.getName()));
	}
	
	public String toString() { 
		return name;
	}

	public int getCount() {
		return count;
	}
	
}
