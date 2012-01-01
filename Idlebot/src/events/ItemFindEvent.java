package events;

import generators.Utilities;

import java.net.MalformedURLException;
import java.net.URL;

import org.pircbotx.Colors;

import bot.IdleBot;
import data.DummyUsable;
import data.Monster;
import data.Player;
import data.Usable;

public class ItemFindEvent {

	static String[] events;
	
	static {
		initialize();
	}

	public static void initialize() {
		try {
			events = Utilities.loadFileNoArray(new URL("http://idlemaster.googlecode.com/svn/trunk/Idlebot/data/events/item_find.txt")).toArray(new String[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public ItemFindEvent(Player p){
		this(p, null);
	}
	
	public ItemFindEvent(Player p, Monster m) {
		DummyUsable u = DummyUsable.dummyItems[(int) (Math.random() * (DummyUsable.dummyItems.length-1))];
		if(m != null) {
			IdleBot.botref.messageChannel(Colors.DARK_BLUE+p.getName() + " found "+u.name+" in "+m.getName()+"'s corpse!");
		} else {
			modifyMessage(p, u, events[(int) (Math.random() * (events.length-1))]);
		}
		p.addItem(new Usable(u.name));
	}
	
	private void modifyMessage(Player p, DummyUsable u, String s) {
		IdleBot.botref.messageChannel(Colors.DARK_BLUE+s.replaceAll("%player", p.getName()).replaceAll("%item", u.name));
	}
}
