package events;

import java.util.ArrayList;

import bot.IdleBot;
import data.Playable;

public class TeamEvent {
	
	public TeamEvent() {
		ArrayList<Playable> group = new ArrayList<>();
		int tries = 0;
		boolean isPlayer=false;
		if(Math.random() > 0.3)  isPlayer = true;
		while(group.size() < 4 && tries++ < 10) {
			Playable p = isPlayer ? IdleBot.botref.getRandomPlayer() : IdleBot.botref.getRandomMonster();
			if(p.getGroup() == null) {
				group.add(p);
				p.setGroup(group);
			}
		}
		
		if(group.size() == 0) return;
		
		if(group.size() == 1) {
			group.get(0).setGroup(null);
			return;
		}
		
		IdleBot.botref.messageChannel("A new group of "+(isPlayer ? "players" : "monsters")+" has formed: "+group);
		
	}
	
}
