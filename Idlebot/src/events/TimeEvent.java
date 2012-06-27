package events;

import generators.Utilities;

import java.net.MalformedURLException;
import java.net.URL;

import org.pircbotx.Colors;

import bot.IdleBot;
import data.Playable.Alignment;
import data.Player;

public class TimeEvent {

	public enum Type {
		Blessing, Fatehand, Forsaken
	};

	static String[] goodEvents;
	static String[] badEvents;
	
	static {
		initialize();
	}

	public static void initialize() {
		try {
			goodEvents = Utilities.loadFileNoArray(new URL("http://idlemaster.googlecode.com/svn/trunk/Idlebot/data/events/time_bless.txt")).toArray(new String[0]);
			badEvents = Utilities.loadFileNoArray(new URL("http://idlemaster.googlecode.com/svn/trunk/Idlebot/data/events/time_forsake.txt")).toArray(new String[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public TimeEvent(Player target) {
		this(target, null);
	}
	
	public TimeEvent(Player target,Type cusType) {
		if(target == null) return;
		Type type = cusType != null ? cusType : Type.values()[(int) (Math.random() * (Type.values().length-1))];
		switch(type) {
		case Blessing:
			if(cusType!=null || Battle.prob(100 - target.getLevel()) && Battle.prob((int)(15*getModifier(target.getAlignment(), type)))) {
				target.stats.blessed++;
				doStuff(goodEvents[(int) (Math.random() * (goodEvents.length-1))], target, false);
			}
			break;
		case Fatehand:
			if(cusType!=null || Battle.prob(100 - target.getLevel()) && Battle.prob((int)(1*getModifier(target.getAlignment(), type))) && Battle.prob(20)) {
				target.stats.fatehand++;
				doStuff("met with the hand of fate", target, null);
			}
			break;
		case Forsaken:
			if(cusType!=null || Battle.prob(100 - target.getLevel()) && Battle.prob((int)(7*getModifier(target.getAlignment(), type)))) {
				target.stats.forsaken++;
				doStuff(badEvents[(int) (Math.random() * (badEvents.length-1))], target, true);
			}
			break;
		}
	}
	
	private void doStuff(String message, Player target, Boolean negative) {
		int perc;
		message = Event.replaceGender(message, target);
		if(negative!=null) {
			perc = (int) (Math.random() * 16)+1;
			perc = negative ? -perc : perc;
		} else {
			perc = (int) (Math.random() * 80)+1;
			perc = Math.random() > 0.5 ? -perc : perc;
		}
		long timeMod = (long) (((Math.pow(1.16, target.getLevel() ))*650000) * (perc/100.0));
		if(negative == null) {
			IdleBot.botref.messageChannel((timeMod < 0 ? Colors.RED : Colors.DARK_GREEN) + target.getName() + " " + message + ", "+((timeMod < 0 ? "adding " : "removing ")+ IdleBot.botref.ms2dd(Math.abs(timeMod)) + (timeMod < 0 ? " to " : " from ")+Event.replaceGender("his/her level timer! ["+Math.abs(perc)+"%]", target)));
		} else {
			IdleBot.botref.messageChannel((timeMod < 0 ? Colors.RED : Colors.DARK_GREEN) + target.getName() + " " + message + " ["+IdleBot.botref.ms2dd(Math.abs(timeMod))+"]");
		}
		target.modifyTime(timeMod);
	}

	public float getModifier(Alignment align, Type type) {
		switch (type) {
		case Blessing:
			if(align == Alignment.Good) return 1.5f;
			return 0.7f;
		case Fatehand:
			if(align == Alignment.Neutral) return 3;
			return 1;
		case Forsaken:
			if(align == Alignment.Evil) return 4.3f;
			return 0.5f;
		default:
			return 1;
		}
	}
}
