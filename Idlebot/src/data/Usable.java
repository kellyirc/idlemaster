package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import data.Playable.Alignment;

import events.*;
import generators.MonsterGenerator;

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
		case "philosopherstone": case "wingedshoes": return false;
		case "fortunecookie":
			doFortuneCookie(p);
			break;
		case "minuteglass":
			p.modifyTime(60000);
			break;
		case "hourglass":
			p.modifyTime(3600000);
			break;
		case "dayglass":
			p.modifyTime(86400000);
			break;
		case "weekglass":
			p.modifyTime(604800000);
			break;
		case "lifeglass":
			p.levelUp();
			break;
		case "crystalshard":
			if(r.nextInt(100) > 35) {
				Player rand = IdleBot.botref.getRandomPlayer();
				IdleBot.botref.messageChannel(".. and was whisked away to "+rand.getName()+"!");
				p.warp(rand);
			}
			else {
				IdleBot.botref.messageChannel(".. and was whisked away!");
				p.warp();
			}
			break;
		case "crystalball":
			p.stats.cataCaused++;
			new Cataclysm();
			break;
		case "karmacookie":
			doKarma(p);
			break;
		case "sledgehammer":
			doSledgehammer(p);
			break;
		case "fightshard":
			doFight(p);
			break;
		case "duelshard":
			doDuel(p);
			break;
		case "goldshard":
			doTeamBattle();
			break;
		case "starshard":
			doBossBattle();
			break;
		case "genielamp":
			p.levelUp();
			break;
		case "geniilamp":
			break;
		}
		count--;
		p.stats.itemUses++;
		return true;
	}

	private void doFight(Player p) {
		new Battle(p, MonsterGenerator.generateMonster(null, -1));
	}

	private void doDuel(Player p) {
		Player left = p;
		Player right;
		do {
			right = IdleBot.botref.getRandomPlayer();
		} while(right.equals(left));
		new Battle(left, right);
	}

	private void doTeamBattle() {
		ArrayList<Player> left = new ArrayList<>();
		ArrayList<Player> right = new ArrayList<>();
		LinkedList<Player> players = IdleBot.botref.getOnlinePlayers();
		if(players.size() < 2) {
			IdleBot.botref.messageChannel(".. but there aren't enough players online!");
			return;		
		}
		Collections.shuffle(players);
		for(int i=0; i<players.size(); i++) {
			if(i%2 == 0) right.add(players.get(i));
			else left.add(players.get(i));
		}
		new Battle(left, right);
	}

	private void doBossBattle() {
		ArrayList<Player> players = new ArrayList<>(IdleBot.botref.getOnlinePlayers());
		ArrayList<Monster> bossguy = new ArrayList<>();
		bossguy.add(MonsterGenerator.generateMonster(Alignment.Evil, (int) (Math.random() * MonsterGenerator.bossct)));
		new Battle(players, bossguy);
	}

	private void doKarma(Player p) {
		if(p.stats.cataCaused > p.stats.cataSuffered) {
			IdleBot.botref.messageChannel(".. and lost two levels!");
			p.setLevel((short) (p.getLevel()-2));
		} else if(p.stats.battlesWon > p.stats.battlesLost){
			IdleBot.botref.messageChannel(".. and gained level time!");
			p.modifyTime(-7500000);
		} else if(p.stats.battlesWon < p.stats.battlesLost) {
			IdleBot.botref.messageChannel(".. and lost level time!");
			p.modifyTime(7500000);
		}
	}

	private void doSledgehammer(Player p) {
		Item left = ItemEvent.getRandomItem(p);
		Item right;
		do {
			right = ItemEvent.getRandomItem(p);
		} while(left.equals(right));
		IdleBot.botref.messageChannel(p.getName() + " smashed at "+left.getName()+", causing it to increase in power ("+left.getValue()+"->"+Math.round(left.getValue()*1.5)+"). The reverberation caused "+right.getName()+" to nearly shatter ("+right.getValue()+"->"+Math.round(right.getValue()*0.5)+")!");
		left.setValue((int) (left.getValue()*1.5));
		right.setValue((int) (right.getValue()*0.5));
	}
	
	private String announceMessage(String name) {
		switch(name) {
		case "fortunecookie":
			return "%player ate a fortune cookie...";
		case "minuteglass":
			return "%player flipped over his minuteglass!";
		case "hourglass":
			return "%player flipped over his hourglass!";
		case "dayglass":
			return "%player flipped over his dayglass!";
		case "weekglass":
			return "%player flipped over his weekglass!";
		case "lifeglass":
			return "%player flipped over his lifeglass!";
		case "crystalshard":
			return "%player grips his/her crystal shard tightly..";
		case "crystalball":
			return "%player gazes deeply into his/her crystal ball..";
		case "karmacookie":
			return "%player takes a bite out of karma!";
		case "sledgehammer":
			return "%player readies his/her sledgehammer!";
		case "duelshard":
			return "%player is getting bloodthirsty, and tightly grips his/her duelshard!";
		case "fightshard":
			return "%player is ready for some fresh meat, and tightly grips his/her fightshard!";
		case "goldshard":
			return "%player is causing some chaos!";
		case "starshard":
			return "%player wishes death upon all others in the world!";
		case "boo":
		case "megaboo":
		case "pocketwatch":
		case "bomb":
		case "mirv":
		case "lottoticket":
		case "luckydice":
		case "pandorasbox":
		case "wishingwell":
		case "wishingfountain":
		case "darkmirror":
		case "genielamp":
		case "geniilamp":
		case "philosopherstone": case "wingedshoes": return "";
		default:
			return "THIS MESSAGE IS NOT CORRECT FOR "+name;
		}
	}
	
	private void doFortuneCookie(Player p) {
		switch(r.nextInt(13)) {
		case 1: case 2:
			IdleBot.botref.messageChannel("... and it tasted delicious!");
			new TimeEvent(p,TimeEvent.Type.Blessing);
			break;
		case 4: case 5:
			IdleBot.botref.messageChannel("... and it tasted delicious!");
			new ItemEvent(p,true);
			break;
		case 7: case 8:
			IdleBot.botref.messageChannel("... and it tasted delicious!");
			new MoneyEvent(p, true);
			break;
		case 10: case 11:
			IdleBot.botref.messageChannel("... but nothing happened!");
			break;
		case 12: case 9:
			IdleBot.botref.messageChannel("... but it was spoiled!");
			new MoneyEvent(p, false);
			break;
		case 13: case 6:
			IdleBot.botref.messageChannel("... but it was spoiled!");
			new ItemEvent(p, false);
			break;
		case 0: case 3:
			IdleBot.botref.messageChannel("... but it was spoiled!");
			new TimeEvent(p, TimeEvent.Type.Forsaken);
			break;
		}
	}

	private void announce(String s, Player p) {
		if(!s.equals(""))IdleBot.botref.messageChannel(s.replaceAll("%player", p.getName()));
	}
	
	public String toString() { 
		return name;
	}

	public int getCount() {
		return count;
	}
	
}
