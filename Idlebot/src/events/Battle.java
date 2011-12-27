package events;

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
				p.health = p.calcTotal(null);
				emotional += p.calcTotal(Type.Emotional);
				spiritual += p.calcTotal(Type.Spiritual);
			}
		}
		
		public void timeMod(long l) {
			for(Playable p : members) { 
				if(p instanceof Player){
					((Player) p).modifyTime(l);
					IdleBot.botref.messageChannel(p.getName()+" got "+IdleBot.botref.ms2dd(Math.abs(l))+(l>0 ? " removed from " : " added to ") + "his/her level timer!");
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
	Random rand = new Random();
	
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
	
	public boolean prob(int i) {
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
			IdleBot.botref.messageChannel(BATTLE + "A crushing blow by "+left+" deals a staggering "+Colors.PURPLE+left.emotional+Colors.NORMAL+" emotional damage to "+right+".");
			right.takeDamage(left.leader, left.emotional);
		}
		if(right.emotional > 0) {
			IdleBot.botref.messageChannel(BATTLE + "A smashing blow by "+right+" inflicts an intense "+Colors.PURPLE +right.emotional+Colors.NORMAL+" emotional damage to "+left+".");
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
		//do attacks, spells, and dodging
		int damage = rand.nextInt(left.calcTotal(Type.Physical));
		IdleBot.botref.messageChannel(BATTLE + left.getBattleName()+" took a swing at "+right.getBattleName()+" with his/her "+getWeapon(left) + " for "+Colors.RED+damage+Colors.NORMAL+" damage!");
		if(right.getAlignment() == Alignment.Good && prob(4)) {
			IdleBot.botref.messageChannel(BATTLE + "..but "+right.getBattleName()+" dodged!");
		}
		right.health -= damage;
	}
	
	private void run() {
		initialize();
		while(left.isAlive() && right.isAlive()) {
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
		if(left.spiritual > 0) {
			IdleBot.botref.messageChannel(BATTLE + left+" whittles away the spirit of "+right+" by "+Colors.PURPLE +left.spiritual+Colors.NORMAL+".");
			right.takeDamage(left.leader, left.spiritual);
		}
		if(right.spiritual > 0) {
			IdleBot.botref.messageChannel(BATTLE + right+" prods "+left+" for "+Colors.PURPLE +right.spiritual+Colors.NORMAL+" spiritual damage.");
			left.takeDamage(right.leader, right.spiritual);
		}
	}

	private void roundStatistics() {
		IdleBot.botref.messageChannel(BATTLE + "Round Statistics: "+left.toBattleString() + " " + right.toBattleString());
	}
	
	private void initialSpeech() {
		if(left.members.size() == 1 && right.members.size() == 1) {
			IdleBot.botref.messageChannel(Colors.RED+BATTLE + left + " is raging up to "+right+"..!");
		}
	}

	//do the winners dance (steal, critical strike if player->player, dropItem if player->monster)
	private void victory(Team victors, Team losrars) {
		IdleBot.botref.messageChannel(Colors.DARK_GREEN+BATTLE + victors + " won the battle!");
		long timeMod = 757 * Math.abs(victors.getRemainingLife() - losrars.getRemainingLife()) * (((victors.getTotalLevel() - losrars.getTotalLevel()) / 4)+1);
		victors.timeMod(timeMod);
		losrars.timeMod(-timeMod/2);
	}

	private void kill(Playable second, Playable first) {
		IdleBot.botref.messageChannel(Colors.RED+BATTLE + second + " killed "+first+".");
		
	}
	
}
