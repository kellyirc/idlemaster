package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.pircbotx.Colors;

import data.Item.Type;

import events.Battle;

import bot.IdleBot;

public abstract class Playable {
	
	public enum Alignment { Evil, Good, Neutral }

	public enum Direction { EAST, NORTH, NORTHEAST, NORTHWEST, SOUTH, SOUTHEAST, SOUTHWEST, WEST }
	public enum Slot { Body, Charm, Feet, Finger, Hands, Head, Legs, Neck, Shield, Weapon }
	
	public static final double BATTLE_MULTIPLIER = 1.5;
	
	public static final int MAX_X = 250;;
	public static final int MAX_Y = 250;;
	protected Alignment alignment;;

	protected String classType;
	
	protected HashMap<Slot, Item> equipment = new HashMap<>();

	private transient volatile ArrayList<? extends Playable> group;
	
	public transient int health = 0;
	
	protected short level = 0;

	protected String name;

	protected int x = 0;

	protected int y = 0;

	public Playable(String name2, String classtype2, Alignment align) {
		this.name = name2;
		this.classType = classtype2;
		this.alignment = align;
	}

	private boolean aboveX() {
		return x-1 > 0;
	}

	private boolean aboveY() {
		return y-1 > 0;
	}
	public int calcTotal(data.Item.Type type) {
		int rev = 0;
		for(Item i : equipment.values()) {
			if(type!=null && i.getType() == type || type == null) rev += i.getValue();
		}
		//if(type == null && this instanceof Monster) rev += ((Monster)this).getBonus();
		return rev;
	}
	private boolean canBattle(Playable playable, Playable other) {
		if(playable.group!=null) {
			
		}
		if(playable.getGroup()!= null && other.getGroup()!= null && playable.getGroup().equals(other.getGroup())) return false;
		return ( !isWithinLevel(playable, other) && !isWithinRange(playable, other));
	}
	public boolean canEquip(Slot s, Item i) {
		if(i.getItemClass() == data.Item.ItemClass.Avatar)
			return true;
		
		Item current = equipment.get(s);
		
		if(current == null) return true;
		
		if(current.getValue() > i.getValue()) return false;
		
		if(i.getValue() > level * ((level/7)+2)*1.5) return false;
		
		return true;
	}

	public void dropItem() {
		
	}

	public void engage(Playable other) {
		if(canBattle(this, other)) {
			if(this.alignment == Alignment.Good && other.alignment == Alignment.Neutral && Battle.prob(10)) {
				IdleBot.botref.messageChannel(Battle.BATTLE + getName() + " greeted "+other.getName()+" and went on his/her merry way.");
				warp();
				return;
			}
			if(this.alignment == Alignment.Neutral && Battle.prob(20)) {
				IdleBot.botref.messageChannel(Battle.BATTLE + getName() + " glanced at "+other.getName()+" and kept walking.");
				warp();
				return;
			}
			if(this.alignment == Alignment.Evil && Battle.prob(10)){
				IdleBot.botref.messageChannel(Battle.BATTLE + getName() + " snickered as s/he walked by "+other.getName()+".");
				warp();
				if(Battle.prob(10)) {
					Battle.steal(this, other);
				}
				return;
			}
			if(this instanceof Player) ((Player)this).stats.battlesCaused++;
			new Battle(this, other);
		} else {
			if((this.level > other.level + 3 || this.calcTotal(null) > other.calcTotal(null) * BATTLE_MULTIPLIER) && Battle.prob(10)) {
				IdleBot.botref.messageChannel(Battle.BATTLE + getName() + " walked past "+other.getName()+", laughing so hard, s/he was crying.");
			} else if((this.level < other.level - 3 || this.calcTotal(null) * BATTLE_MULTIPLIER < other.calcTotal(null)) && Battle.prob(10)) {
				IdleBot.botref.messageChannel(Battle.BATTLE + other.getName() + " passed by "+getName()+", laughing and gloating.");
			} else if(Battle.prob(10)){
				IdleBot.botref.messageChannel(Battle.BATTLE + other.getName() + " and "+getName() + " wave as they pass by each other.");
			}
			
			if(other instanceof Monster) {
				((Monster) other).die(null);
			}
		}
		warp();
	}

	/**
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}
	public String getBattleName() {
		return Colors.BOLD + getName() + Colors.NORMAL;
	}

	/**
	 * @return the classType
	 */
	public String getClassType() {
		return classType;
	}

	public String getColorNumber(String color, Type type) {
		return calcTotal(type) == 0 ? "" : "["+color+calcTotal(type)+Colors.NORMAL+"] ";
	}

	/**
	 * @return the equipment
	 */
	public Set<Entry<Slot, Item>> getEquipment() {
		return equipment.entrySet();
	}
	
	public HashMap<Slot,Item> getEquipmentRaw() {
		return equipment;
	}
	
	/**
	 * @return the group
	 */
	public ArrayList<? extends Playable> getGroup() {
		return group;
	}
	
	/**
	 * @return the level
	 */
	public short getLevel() {
		return level;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	
	private boolean isWithinLevel(Playable left, Playable right) {
		return (calcLevelGroup(left)*BATTLE_MULTIPLIER*3 < calcLevelGroup(right) || calcLevelGroup(right)*BATTLE_MULTIPLIER*3 < calcLevelGroup(left));
	}
	
	private boolean isWithinRange(Playable left, Playable right) {
		return (calcTotalGroup(left)*BATTLE_MULTIPLIER < calcTotalGroup(right) || calcTotalGroup(right)*BATTLE_MULTIPLIER < calcTotalGroup(left));
	}
	
	private int calcLevelGroup(Playable left) {
		if(left.getGroup() == null) return left.level;
		int rev = 0;
		for(Playable p : left.getGroup()) {
			rev += p.getLevel();
		}
		return rev;
	}
	
	private int calcTotalGroup(Playable left) {
		if(left.getGroup() == null) return left.calcTotal(Type.Physical);
		int rev = 0;
		for(Playable p : left.getGroup()) {
			rev += p.calcTotal(Type.Physical);
		}
		return rev;
	}
	
	/**
	 * @param equipment the equipment to set
	 */
	public void setEquipment(HashMap<Slot, Item> equipment) {
		this.equipment = equipment;
	}

	public void move() {
		if(x<0 || x>MAX_X) x=0;
		if(y<0 || y>MAX_Y) y=0;
		switch(Direction.values()[(int) (Math.random() * (Direction.values().length-1))]) {
		case NORTH:
			if(underY()) move(0,1);
			break;
		case NORTHEAST:
			if(underX() && underY()) move(1,1);
			break;
		case NORTHWEST:
			if(aboveX() && underY()) move(-1,1);
			break;
		case SOUTH:
			if(aboveY()) move(0,-1);
			break;
		case SOUTHEAST:
			if(aboveX() && underY()) move(1,-1);
			break;
		case SOUTHWEST:
			if(aboveX() && aboveY()) move(-1,-1);
			break;
		case EAST:
			if(underX()) move(1,0);
			break;
		case WEST:
			if(aboveX()) move(-1,0);
			break;
		}
		Playable p = IdleBot.botref.findPlayableByCoordinates(this);
		if(p!=null) {
			engage(p);
		}
	}
	
	public void move(int xoff, int yoff) {
		x += xoff;
		y += yoff;
	}
	
	/**
	 * @param alignment the alignment to set
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}
	
	/**
	 * @param classType the classType to set
	 */
	public void setClassType(String classType) {
		this.classType = classType;
	}
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(ArrayList<? extends Playable> group) {
		this.group = group;
	}
	
	/**
	 * @param level the level to set
	 */
	public void setLevel(short level) {
		this.level = level;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	public void takeTurn() {
		move();
	}
	
	public String toBattleString() {
		return getName() + Colors.BOLD +" ("+Colors.RED+health+Colors.NORMAL+"/"+Colors.RED+calcTotal(null)+Colors.NORMAL+") "+getColorNumber(Colors.BROWN, Type.Physical)+getColorNumber(Colors.BLUE, Type.Magical)+getColorNumber(Colors.PURPLE, Type.Emotional)+getColorNumber(Colors.DARK_BLUE, Type.Spiritual);
	}
	
	private boolean underX() {
		return x+1 < MAX_X;
	}
	
	private boolean underY() {
		return y+1 < MAX_Y;
	}
	
	public void warp() {
		x = (int) (Math.random() * MAX_X);
		y = (int) (Math.random() * MAX_Y);
	}
	
	public void warp(Player p) {
		setX(p.getX());
		setY(p.getY());
	}
}