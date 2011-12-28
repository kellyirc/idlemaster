package events;

import generators.SpellGenerator;
import generators.SpellGenerator.Spell;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;

import org.pircbotx.Colors;

import bot.IdleBot;

import data.Item;
import data.Playable;
import data.Item.Type;
import data.Playable.Alignment;
import data.Playable.Slot;
import data.Player;
import data.Monster;

public class Battle {
	public static final String BATTLE = " *** ";
	
	private class Team {
		public ArrayList<Playable> members;
		public Playable leader;
		public int spiritual = 0;
		public int emotional = 0;
		
		public Team(ArrayList<Playable> team) {
			members = team;
			leader = team.get(rand.nextInt(team.size()));
		}
		
		public String toString() {
			return members.size() > 1 ? "Team "+leader.getName() + " "+members : leader.getName();
		}
		
		public void initialize() {
			for(Playable p : members) {
				if(p instanceof Player) isMonsterOnly = false;
				if(!((p instanceof Monster) && ((Monster) p).strings!=null && p.health > 0)) p.health = p.calcTotal(null) + (p instanceof Monster ? ((Monster)p).getBonus() : 0);
				emotional += p.calcTotal(Type.Emotional);
				spiritual += p.calcTotal(Type.Spiritual);
			}
		}
		
		public void timeMod(long l) {
			for(Playable p : members) { 
				if(p instanceof Player){
					((Player) p).modifyTime(l);
					battleMessage(p.getName()+" got "+IdleBot.botref.ms2dd(Math.abs(l))+(l>0 ? " removed from " : " added to ") + "his/her level timer!");
				} 
			}
		}
		
		public long getRemainingLife() { 
			long ret = 0;
			for(Playable p : members) { 
				ret += p.health;
			}
			return ret;
		}
		
		public int getTotalLevel() {
			int ret = 0;
			for(Playable p : members) {
				ret += p.getLevel();
			}
			return ret;
		}
		
		public void takeDamage(Playable opp, int damage) {
			for(Playable p : members) {
				p.health -= damage;
				if(p.health <= 0) {
					kill(opp, p);
				}
			}
		}
		
		public boolean isAlive() {
			for(Playable p : members) { 
				if(p.health > 0) return true;
			}
			return false;
		}

		public Playable pickAliveMember() {
			Playable p;
			do {
				p = members.get(rand.nextInt(members.size()));
			} while(p.health <= 0);
			return p;
		}
		
		public String toBattleString() {
			if(members.size() == 1) return leader.toBattleString();
			String builder = "[ ";
			for(Playable p : members) {
				if(p.health <= 0) builder += Colors.DARK_GRAY + p.toBattleString() + Colors.NORMAL;
				else builder += p.toBattleString();
				builder += " ";
			}
			builder += "]";
			return builder;
		}
	}
	
	Team left, right;
	static Random rand = new Random();
	private SpellGenerator spellGen = new SpellGenerator();
	private int turns = 0;
	boolean isMonsterOnly = true;
	
	public Battle(ArrayList<Playable> left, ArrayList<Playable> right) {
		this.left = new Team(left);
		this.right = new Team(right);
		run();
	}
	
	public Battle(Playable playable, Playable other) {
		ArrayList<Playable> left = new ArrayList<>();
		ArrayList<Playable> right = new ArrayList<>();
		left.add(playable);
		right.add(other);
		this.left = new Team(left);
		this.right = new Team(right);
		run();
	}
	
	public static boolean prob(int i) {
		return rand.nextInt(100) < i;
	}
	
	public void initialize() {
		left.initialize();
		right.initialize();
		initialSpeech();
		emotionalDamage();
	}

	private void emotionalDamage() {
		if(left.emotional > 0) {
			battleMessage(BATTLE + "A crushing blow by "+left+" deals a staggering "+Colors.PURPLE+left.emotional+Colors.NORMAL+" emotional damage to "+right+".");
			right.takeDamage(left.leader, left.emotional);
		}
		if(right.emotional > 0) {
			battleMessage(BATTLE + "A smashing blow by "+right+" inflicts an intense "+Colors.PURPLE +right.emotional+Colors.NORMAL+" emotional damage to "+left+".");
			left.takeDamage(right.leader, right.emotional);
		}
	}
	
	private Item getWeapon(Playable target) {
		for(Entry<Slot, Item> ent : target.getEquipment()) {
			if(ent.getKey().toString().equals("Weapon")) {
				return ent.getValue();
			}
		}
		return null;
	}

	private void attack(Playable left, Playable right) {
		if(left.getAlignment() == Alignment.Good && turns%2 == 0) {
			Spell s = spellGen.generateGoodSpell(left.calcTotal(Type.Magical));
			battleMessage(BATTLE + left.getBattleName()+" cast "+s+" at "+ right.getName() + " for "+Colors.RED+s.getDamage()+Colors.NORMAL+" damage!");
			right.health -= s.getDamage();
		} else if(left.getAlignment() == Alignment.Evil && turns%4 == 0) {
			Spell s = spellGen.generateEvilSpell(left.calcTotal(Type.Magical));
			battleMessage(BATTLE + left.getBattleName()+" cast "+s+" at "+ right.getName() + " for "+Colors.RED+s.getDamage()+Colors.NORMAL+" damage!");
			right.health -= s.getDamage();
		} else {
			physicalAttack(left, right);
		}
	}

	private void physicalAttack(Playable left, Playable right) {
		int damage = rand.nextInt(left.calcTotal(Type.Physical)+1);
		battleMessage(BATTLE + left.getBattleName()+" took a swing at "+right.getBattleName()+" with his/her "+getWeapon(left) + " for "+Colors.RED+damage+Colors.NORMAL+" damage!");
		if(right.getAlignment() == Alignment.Good && prob(4)) {
			battleMessage(BATTLE + "..but "+right.getBattleName()+" dodged!");
			return;
		} else if(right.getAlignment() == Alignment.Good && prob(20)) {
			damage -= (damage * 0.34);
			battleMessage(BATTLE + "..but "+right.getBattleName()+" parried the blow, reducing the damage to "+Colors.RED+damage+Colors.NORMAL+"!");
			
		} else if(right.getAlignment() == Alignment.Neutral && prob(7)) {
			damage -= (damage * 0.17);
			battleMessage(BATTLE + "..but "+right.getBattleName()+" parried the blow, reducing the damage to "+Colors.RED+damage+Colors.NORMAL+"!");
			
		}
		right.health -= damage;
	}
	
	private void run() {
		initialize();
		while(left.isAlive() && right.isAlive() && ++turns>0) {
			Playable first = left.pickAliveMember();
			Playable second = right.pickAliveMember();
			
			attack(first, second);
			if((first.getAlignment() == Alignment.Evil && prob(7)) || (second.getAlignment() == Alignment.Neutral && prob(1))) {
				attack(first, second);
			}
			if(second.health < 0) {
				kill(first, second);
			}
			
			if(second.health > 0) {
				attack(second, first);
				if((second.getAlignment() == Alignment.Evil && prob(7)) || (second.getAlignment() == Alignment.Neutral && prob(1))) {
					attack(second, first);
				}
			}
			if(first.health < 0) {
				kill(second, first);
			} 

			spiritualDamage();
			roundStatistics();
		}
		if(!left.isAlive()) {
			victory(right, left);
		}
		if(!right.isAlive()) {
			victory(left, right);
		}
	}

	private void spiritualDamage() {
		if(left.isAlive() && left.spiritual > 0) {
			battleMessage(BATTLE + left+" whittles away the spirit of "+right+" by "+Colors.PURPLE +left.spiritual+Colors.NORMAL+".");
			right.takeDamage(left.leader, left.spiritual);
		}
		if(right.isAlive() && right.spiritual > 0) {
			battleMessage(BATTLE + right+" prods "+left+" for "+Colors.PURPLE +right.spiritual+Colors.NORMAL+" spiritual damage.");
			left.takeDamage(right.leader, right.spiritual);
		}
	}

	private void roundStatistics() {
		battleMessage(BATTLE + "Round Statistics: "+left.toBattleString() + " " + right.toBattleString());
	}
	
	private void initialSpeech() {
		if(left.members.size() == 1 && right.members.size() == 1) {
			battleMessage(Colors.RED+BATTLE + left + " is raging up to "+right+"..!");
		}
	}

	//do the winners dance (steal, critical strike if player->player, dropItem if player->monster)
	private void victory(Team victors, Team losrars) {
		battleMessage(Colors.DARK_GREEN+BATTLE + victors + " won the battle!");
		float mod = ((victors.getTotalLevel() - losrars.getTotalLevel()) / 4)+1;
		long timeMod = (long) (757 * Math.abs(victors.getRemainingLife() - losrars.getRemainingLife()) * (mod == 0 ? 1 : mod));
		victors.timeMod(timeMod);
		losrars.timeMod(-timeMod/2);
	}

	private void battleMessage(String string) {
		//if(!isMonsterOnly)
			IdleBot.botref.messageChannel(string);
		
	}

	private void kill(Playable second, Playable first) {
		battleMessage(Colors.RED+BATTLE + second + " killed "+first+".");
		trySteal(second, first);
		if(!(first instanceof Player)) {
			((Monster)first).die(second);
		} else {
			tryCritStrike(second, first);
			((Player)second).stats.battlesWon++;
			((Player)second).stats.battlesLost++;
		}
	}

	private void trySteal(Playable second, Playable first) {
		if(second.getAlignment() == Alignment.Neutral && prob(100)) {
			steal(second, first);
		} else if(second.getAlignment() == Alignment.Evil && prob(getEvilStealProb(first.getAlignment()))) {
			steal(second, first);
		}
	}

	private int getEvilStealProb(Alignment alignment) {
		switch(alignment) {
		case Good: return 15;
		case Neutral: return 10;
		case Evil: return 5;
		default: return 25;
		}
	}

	private void tryCritStrike(Playable second, Playable first) {
		long timeMod = 757 * Math.abs(second.health - first.health) * (((second.getLevel() - first.getLevel()) / 4)+1);

		if(second.getAlignment() == Alignment.Good && first.getAlignment() == Alignment.Evil && prob(80)) {
			battleMessage(Colors.DARK_GREEN+BATTLE + second + " landed a critical final blow, adding "+IdleBot.botref.ms2dd(timeMod/2)+" to "+first+"'s level timer!");
			((Player)first).modifyTime(-timeMod/2);
		} else if(second.getAlignment() == Alignment.Neutral && prob(20)) {
			battleMessage(Colors.DARK_GREEN+BATTLE + second + " landed a critical final blow, adding "+IdleBot.botref.ms2dd(timeMod/5)+" to "+first+"'s level timer!");
			((Player)first).modifyTime(-timeMod/5);
		} else if(second.getAlignment() == Alignment.Evil && first.getAlignment() == Alignment.Good && prob(40)) {
			battleMessage(Colors.DARK_GREEN+BATTLE + second + " landed a critical final blow, adding "+IdleBot.botref.ms2dd(timeMod/3)+" to "+first+"'s level timer!");
			((Player)first).modifyTime(-timeMod/3);
		} else if(second.getAlignment() == Alignment.Evil && first.getAlignment() == Alignment.Neutral && prob(20)) {
			battleMessage(Colors.DARK_GREEN+BATTLE + second + " landed a critical final blow, adding "+IdleBot.botref.ms2dd(timeMod/4)+" to "+first+"'s level timer!");
			((Player)first).modifyTime(-timeMod/4);
		}
	}
	
	public static void steal(Playable left, Playable right) {
		//if(left instanceof Monster) return;
		Slot s = Playable.Slot.values()[rand.nextInt(Playable.Slot.values().length)];
		
		Item old = left.getEquipmentRaw().get(s);
		Item pnew = right.getEquipmentRaw().get(s);
		
		if(pnew.getValue() > old.getValue()) {
			IdleBot.botref.messageChannel(Colors.DARK_BLUE+left.getName()+ " stole "+pnew.getName()+" from "+right.getName()+"!");
			left.getEquipmentRaw().put(s,pnew);
			right.getEquipmentRaw().put(s,old);
		} else {
			IdleBot.botref.messageChannel(Colors.DARK_BLUE+left.getName()+ " would have stolen "+pnew.getName()+" from "+right.getName()+", if it were any good.");
		}
	}
	
}
