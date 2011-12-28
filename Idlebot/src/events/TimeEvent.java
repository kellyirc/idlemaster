package events;

import org.pircbotx.Colors;

import bot.IdleBot;
import data.Playable.Alignment;
import data.Player;

public class TimeEvent {

	public enum Type {
		Blessing, Fatehand, Forsaken
	};

	public TimeEvent(Player target) {
		Type type = Type.values()[(int) (Math.random() * Type.values().length)];
		switch(type) {
		case Blessing:
			if(Battle.prob(100 - target.getLevel()) && Battle.prob((int)(15*getModifier(target.getAlignment(), type)))) {
				doStuff("was blessed", target, false);
			}
			break;
		case Fatehand:
			if(Battle.prob(100 - target.getLevel()) && Battle.prob((int)(1*getModifier(target.getAlignment(), type))) && Battle.prob(20)) {
				doStuff("met with the hand of fate", target, null);
			}
			break;
		case Forsaken:
			if(Battle.prob(100 - target.getLevel()) && Battle.prob((int)(7*getModifier(target.getAlignment(), type)))) {
				doStuff("was forsaken", target, true);
			}
			break;
		}
	}
	
	private void doStuff(String message, Player target, Boolean negative) {
		int perc;
		if(negative!=null) {
			perc = (int) (Math.random() * 16);
			perc = negative ? -perc : perc;
		} else {
			perc = (int) (Math.random() * 80);
			perc = Math.random() > 0.5 ? -perc : perc;
		}
		long timeMod = (long) (((Math.pow(1.16, target.getLevel() ))*650000) * (perc/100.0));
		IdleBot.botref.messageChannel((timeMod < 0 ? Colors.RED : Colors.DARK_GREEN) + target.getName() + " " + message + ", "+((timeMod < 0 ? "adding " : "removing ")+ IdleBot.botref.ms2dd(Math.abs(timeMod)) + (timeMod < 0 ? " to " : " from ")+"his/her level timer! ["+Math.abs(perc)+"%]"));
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
