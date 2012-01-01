package events;

import generators.Utilities;

import java.net.MalformedURLException;
import java.net.URL;

import org.pircbotx.Colors;

import bot.IdleBot;
import data.Player;

public class MoneyEvent {
	
	public static final int percent = 1;
	
	static String[] goodEvents;
	static String[] badEvents;
	
	static {
		initialize();
	}

	public static void initialize() {
		try {
			goodEvents = Utilities.loadFileNoArray(new URL("http://idlemaster.googlecode.com/svn/trunk/Idlebot/data/events/gold_bless.txt")).toArray(new String[0]);
			badEvents = Utilities.loadFileNoArray(new URL("http://idlemaster.googlecode.com/svn/trunk/Idlebot/data/events/gold_forsake.txt")).toArray(new String[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public MoneyEvent(Player p) {
		this(p, null);
	}
	
	public MoneyEvent(Player p, Boolean b) {
		int i = (int) (p.getMoney() * (percent/100.0));
		if((Math.random() > 0.3 || b != null && b) && b!= null && b!=false) {
			IdleBot.botref.messageChannel(modifyMessage(goodEvents[(int) (Math.random() * (goodEvents.length-1))], p, i, true));
			p.stats.moneyFound++;
			p.setMoney(p.getMoney() + i);
		} else if(b==null || !b){
			IdleBot.botref.messageChannel(modifyMessage(badEvents[(int) (Math.random() * (badEvents.length-1))], p, i, false));
			p.stats.moneyLost++;
			p.setMoney(p.getMoney() - i);
		}
	}
	
	private String modifyMessage(String string, Player p, int i, boolean isGood) {
		String message = (isGood ? Colors.DARK_GREEN : Colors.RED) + string.replaceAll("%player", p.getName()).replaceAll("%money", ""+i);
		return message;
	}
}
