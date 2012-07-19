package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map.Entry;

import data.Item.ItemClass;
import data.Playable.Alignment;
import data.Playable.Slot;

import events.*;
import generators.ItemGenerator;
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
			doCrystalShard(p);
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
			doGenieLamp(p);
			break;
		case "geniilamp":
			doGeniiLamp(p);
			break;
		case "boo":
			doBoo();
			break;
		case "megaboo":
			doMegaboo();
			break;
		case "pocketwatch":
			doPocketwatch(p);
			break;
		case "bomb":
			doBomb(p);
			break;
		case "mirv":
			doMIRV(p);
			break;
		case "lottoticket":
			doLotto(p);
			break;
		case "luckydice":
			doDice(p);
			break;
		case "wishingwell":
			doWishingWell(p);
			break;
		case "wishingfountain":
			doWishingFountain();
			break;
		case "pandorasbox":
			doPandorasBox();
			break;
		case "mirror":
			doMirror(p);
			break;
		case "darkmirror":
			doDarkmirror(p);
			break;
		case "abacus":
			new Event(Event.EVENT_MAX_PROB);
			break;
		case "flagofvalor":
			doFlag();
			break;
		}
		count--;
		p.stats.itemUses++;
		return true;
	}

	private void doFlag() {
		if(Math.random() > 0.65) {
			new TeamEvent(true);
		} else {
			new TeamEvent(false);
		}
	}

	private void doDarkmirror(Player p) {
		Battle b = new Battle(p, new Monster("Doppleganger", p.getAlignment()));
		if(b.getVictor().members.contains(p)) {
			generateItem(p, ItemClass.Idle);
		}
	}
	
	private void doMirror(Player p) {
		if(Math.random() > 0.5) {
			if(p.getAlignment() != Alignment.Good) {
				IdleBot.botref.messageChannel("...and felt a warped sense of duty, becoming good!");
				p.setAlignment(Alignment.Good);
				return;
			}
			new TimeEvent(p, TimeEvent.Type.Fatehand);
			
		} else {
			if(p.getAlignment() != Alignment.Evil) {
				IdleBot.botref.messageChannel("...and tasted the blood of the devil, becoming evil!");
				p.setAlignment(Alignment.Evil);
				return;
			}
			new TimeEvent(p, TimeEvent.Type.Fatehand);
		}
	}

	private void doPandorasBox() {
		for(int i=0; i<7; i++) {
			new Cataclysm();
		}
	}

	private void doWishingFountain() {
		for(Player player : IdleBot.botref.getOnlinePlayers()) {
			Item i = ItemEvent.getRandomItem(player);
			IdleBot.botref.messageChannel("Suddenly, "+player.getName()+"'s "+i.getName()+" became more powerful! ["+i.getValue()+"->"+Math.round(i.getValue()*1.1)+"]");
			i.setValue((int) (i.getValue()*1.1));
		}
	}

	private void doGenieLamp(Player p) {
		double d = Math.random();
		IdleBot.botref.messageChannel(p.getName() + " has three wishes!");
		if(d < 0.05) {
			IdleBot.botref.messageChannel("One of them was a level up!");
			p.levelUp();
		} else if(d < 0.5) { 
			IdleBot.botref.messageChannel("One of them was more money!");
			new MoneyEvent(p, true);
		} else {
			IdleBot.botref.messageChannel("One of them was more pain in the world!");
			new TimeEvent(IdleBot.botref.getRandomPlayer(), TimeEvent.Type.Forsaken);
		}
		d = Math.random();
		if(d < 0.1) {
			IdleBot.botref.messageChannel("The second was worldly destruction!");
			p.stats.cataCaused++;
			new Cataclysm();
		} else if(d < 0.3) {
			IdleBot.botref.messageChannel("The second was more material possessions!");
			new ItemFindEvent(p);
		} else {
			IdleBot.botref.messageChannel("The second was world peace!");
		}
		if(Math.random() < 0.3) {
			IdleBot.botref.messageChannel("The third was power!");
			IdleBot.botref.messageChannel(p.getName() +" prayed..");
			generateItem(p);
			
		} else {
			IdleBot.botref.messageChannel("The third was anything in the world!");
			new Event(p,Event.EVENT_MAX_PROB);
		}
	}

	private void doWishingWell(Player p) {
		Player rand;
		if(Math.random() > 0.9) rand = p;
		else rand = IdleBot.botref.getRandomPlayer();
		for(Entry<Slot, Item> i : rand.getEquipment()) {
			i.getValue().setValue((int) (i.getValue().getValue()*1.05));
		}
		IdleBot.botref.messageChannel("...and suddenly, "+rand.getName()+" feels more powerful!");
	}

	private void doDice(Player p) {
		int rolls=0;
		while(rolls++>0 && ( (int)(Math.random()*6) == (int)(Math.random()*6) ));
		if(rolls == 1) {
			IdleBot.botref.messageChannel("...but couldn't roll the dice to save %hisher life!",p);
		} else {
			long gain = (long) (rolls*(Math.pow(1.16, p.getLevel()))*90000);
			IdleBot.botref.messageChannel("...and got "+IdleBot.botref.ms2dd(gain)+" taken away from %hisher level timer!",p);
			p.modifyTime(gain);
		}
	}

	private void doLotto(Player p) {
		int value=10;
		while(Battle.prob(33)) {
			value*=10;
		}
		IdleBot.botref.messageChannel("...and won "+value+" gold!");
		p.setMoney(p.getMoney()+value);
	}

	private void doMIRV(Player p) {
		for(int i=0; i<7; i++) {
			doBomb(p);
		}
	}

	private void doBomb(Player p) {
		Player rand = IdleBot.botref.getRandomPlayer();
		Item left = ItemEvent.getRandomItem(rand);
		if(Math.random() > 0.8) {
			IdleBot.botref.messageChannel("..."+rand.getName()+" dodged the bomb!");
		} else {
			long l = (long) (Math.random() * 500000);
			IdleBot.botref.messageChannel("..."+rand.getName()+" got hit by the bomb, causing "+left.getName()+" to crack ("+left.getValue()+"->"+Math.round(left.getValue()*0.8)+") and taking a "+IdleBot.botref.ms2dd(l)+" to %hisher level timer!",rand);
			left.setValue((int) (left.getValue() * 0.8));
			rand.modifyTime(l);
		}
	}

	private void doCrystalShard(Player p) {
		if(Math.random() > 0.35) {
			Player rand;
			do {
				rand = IdleBot.botref.getRandomPlayer();
			} while(rand.equals(p));
			IdleBot.botref.messageChannel(".. and was whisked away to "+rand.getName()+"!");
			p.warp(rand);
		} else {
			IdleBot.botref.messageChannel(".. and was whisked away!");
			p.warp();
		}
	}
	
	private void doBoo() {
		Player rand = IdleBot.botref.getRandomPlayer();
		Player recip = IdleBot.botref.getRandomPlayer();
		int i = (int) ((Math.random() * rand.getMoney()-1)+1);
		IdleBot.botref.messageChannel("...the ghost took "+i+" gold from "+rand.getName()+"!");
		if(rand.equals(recip)) {
			IdleBot.botref.messageChannel("...and gave it right back!");
			return;
		} else {
			IdleBot.botref.messageChannel("...and gave it to "+recip.getName()+"!");
			rand.setMoney(rand.getMoney() - i);
			recip.setMoney(recip.getMoney() + i);
		}
	}

	private void doPocketwatch(Player p) {
		if(Math.random() > 0.5) {
			IdleBot.botref.messageChannel("...and realized %she was late for a very important date!",p);
			new TimeEvent(p, TimeEvent.Type.Forsaken);
			new ItemEvent(p, false);
			new MoneyEvent(p, false);
		} else {
			IdleBot.botref.messageChannel("...and realized %she is meant to arrive precisely when %she feels like it!",p);
			new TimeEvent(p, TimeEvent.Type.Blessing);
			new ItemEvent(p, true);
			new MoneyEvent(p, true);
		}
	}

	private void doMegaboo() {
		Player left = IdleBot.botref.getRandomPlayer();
		Player right;
		do {
			right = IdleBot.botref.getRandomPlayer();
		} while(left.equals(right));
		if(!Battle.steal(left, right)) {
			IdleBot.botref.messageChannel("...but it failed!");
		}
	}

	private void doGeniiLamp(Player p) {
		int i = (int) (Math.random() * 100);
		if(i < 10) {
			generateItem(p);
		} else if(i < 30) {
			IdleBot.botref.messageChannel("...but nothing happened!");
		} else if(i < 65) {
			IdleBot.botref.messageChannel("...and got a terrible occurrence!");
			int ii = (int) (Math.random() * 3);
			switch(ii) {
			case 0:
				new ItemEvent(p, false);
				break;
			case 1:
				new TimeEvent(p, TimeEvent.Type.Forsaken);
				break;
			case 2:
				new MoneyEvent(p, false);
				break;
			}
		} else if(i < 85) {
			IdleBot.botref.messageChannel("...and lost a level!");
			p.setLevel((short) (p.getLevel()-1));
		} else {
			IdleBot.botref.messageChannel("...and got a cataclysmic occurrence!!");
			p.stats.cataCaused++;
			new Cataclysm();
		}
	}
	
	public static void generateItem() {
		try{
			generateItem(IdleBot.botref.getRandomPlayer());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void generateItem(Player p) {
		generateItem(p, ItemClass.Avatar);
	}

	private static void generateItem(Player p, ItemClass i) {
		Slot slot = Playable.Slot.values()[(int) (Math.random() * Playable.Slot.values().length)];
		Item item = ItemGenerator.generateItem(slot, i, null);
		if(p.tryEquip(item, slot)) { 
			IdleBot.botref.messageChannel("...and got a/n "+item.getName() + " <<"+item.getValue()+">>!");
		} else
			IdleBot.botref.messageChannel("...but had to sell %hisher new "+item.getName() + "...", p);
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
		bossguy.add(MonsterGenerator.generateMonster(Alignment.Evil, (int) (Math.random() * (MonsterGenerator.bossct-1))));
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
			return "%player flipped over %hisher minuteglass!";
		case "hourglass":
			return "%player flipped over %hisher hourglass!";
		case "dayglass":
			return "%player flipped over %hisher dayglass!";
		case "weekglass":
			return "%player flipped over %hisher weekglass!";
		case "lifeglass":
			return "%player flipped over %hisher lifeglass!";
		case "crystalshard":
			return "%player grips %hisher crystal shard tightly..";
		case "crystalball":
			return "%player gazes deeply into %hisher crystal ball..";
		case "karmacookie":
			return "%player takes a bite out of karma!";
		case "sledgehammer":
			return "%player readies %hisher sledgehammer!";
		case "duelshard":
			return "%player is getting bloodthirsty, and tightly grips %hisher duelshard!";
		case "fightshard":
			return "%player is ready for some fresh meat, and tightly grips %hisher fightshard!";
		case "goldshard":
			return "%player is causing some chaos!";
		case "starshard":
			return "%player wishes death upon all others in the world!";
		case "boo":
			return "%player calls upon the ghost of gold thievery!";
		case "megaboo":
			return "%player calls upon the idol of item thievery!";
		case "pocketwatch":
			return "%player looks at %hisher pocketwatch...";
		case "bomb":
			return "%player lights the fuse of a bomb and tosses it in the air!";
		case "mirv":
			return "%player fires a MIRV. Uh-oh!";
		case "lottoticket":
			return "%player reads the winning lotto numbers...";
		case "luckydice":
			return "%player rolls a pair of ancient dice...";
		case "pandorasbox":
			return "%player opened pandora's box!";
		case "wishingwell":
			return "%player took a sip of water from %hisher wishing well, which seems to just follow %himher around...";
		case "wishingfountain":
			return "%player took a dip in %hisher wishing fountain...";
		case "mirror":
			return "%player gazes into a mirror...";
		case "darkmirror":
			return "%player gazes deeply into a darkened mirror...";
		case "genielamp":
			return "%player rubs a shiny gold lamp...";
		case "geniilamp":
			return "%player rubs an old bronze lamp...";
		case "abacus":
			return "%player fiddles with %hisher abacus...";
		case "flagofvalor":
			return "%player raises the flag of valor!";
		case "philosopherstone": case "wingedshoes": return "";
		default:
			return "THIS MESSAGE IS NOT CORRECT FOR "+name;
		}
	}
	
	private void doFortuneCookie(Player p) {
		switch((int)(Math.random()*13)) {
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
		if(!s.equals(""))IdleBot.botref.messageChannel(s.replaceAll("%player", p.getName()),p);
	}
	
	public String toString() { 
		return name;
	}

	public int getCount() {
		return count;
	}
	
}
