package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.pircbotx.Colors;

import data.Item.Type;

import events.Battle;
import events.Event;

import bot.IdleBot;

public abstract class Playable {
	
	public enum Alignment { Evil, Good, Neutral }

	public enum Direction { EAST, NORTH, NORTHEAST, NORTHWEST, SOUTH, SOUTHEAST, SOUTHWEST, WEST }
	public enum Slot { Body, Charm, Feet, Finger, Hands, Head, Legs, Neck, Shield, Weapon }
	
	public static final double BATTLE_MULTIPLIER = 1.5;
	public static final double TIME_MULTIPLIER = 1.16;
	
	public static final int MAX_X = 250;
	public static final int MAX_Y = 250;
	protected Alignment alignment;

	private Boolean isMale = null;
	protected String classType;
	
	protected ConcurrentHashMap<Slot, Item> equipment = new ConcurrentHashMap<>();

	public void setEquipment(ConcurrentHashMap<Slot, Item> equipment) {
		this.equipment = equipment;
	}

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
	
	public Boolean getIsMale() {
		return isMale;
	}

	public void setIsMale(Boolean isMale) {
		this.isMale = isMale;
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
		return Math.max(rev,0);
	}
	
	public boolean canEquip(Slot s, Item i) {
		
		Item current = equipment.get(s);
		
		if(current == null) return true;
		
		if(current.getValue() > i.getValue()) return false;
		
		if(i.getValue() > level * ((level/1.3)+15)*1.89) return false;
		
		return true;
	}

	public void dropItem() {
		
	}

	public void engage(Playable other) {
		if(this.canBattle(other)) {
			if(this.alignment == Alignment.Good && other.alignment == Alignment.Neutral && Battle.prob(5)) {
				IdleBot.botref.messageChannel(Event.replaceGender(Battle.BATTLE + getName() + " greeted "+other.getName()+" and went on %hisher merry way.",this));
				warp();
				return;
			}
			if(this.alignment == Alignment.Neutral && Battle.prob(10)) {
				IdleBot.botref.messageChannel(Battle.BATTLE + getName() + " glanced at "+other.getName()+" and kept walking.");
				warp();
				return;
			}
			if(this.alignment == Alignment.Evil && Battle.prob(5)){
				IdleBot.botref.messageChannel(Event.replaceGender(Battle.BATTLE + getName() + " snickered as %she walked by "+other.getName()+".", this));
				warp();
				if(Battle.prob(15)) {
					Battle.steal(this, other);
				}
				return;
			}
			if(this instanceof Player) ((Player)this).stats.battlesCaused++;
			new Battle(this, other);
		} else {
			if(Battle.prob(10)) {
				if((this.level > other.level + 10 || this.calcTotal(null) > other.calcTotal(null) * BATTLE_MULTIPLIER)) {
					IdleBot.botref.messageChannel(Event.replaceGender(Battle.BATTLE + getName() + " walked past "+other.getName()+", laughing so hard, %she was crying.",this));
				} else if((this.level < other.level - 10 || this.calcTotal(null) * BATTLE_MULTIPLIER < other.calcTotal(null))) {
					IdleBot.botref.messageChannel(Battle.BATTLE + other.getName() + " passed by "+getName()+", laughing and gloating.");
				} else {
					IdleBot.botref.messageChannel(Battle.BATTLE + other.getName() + " and "+getName() + " wave as they pass by each other.");
				}
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
	
	public Collection<Item> getEquipmentItems() {
		return equipment.values();
	}
	
	
	public ConcurrentHashMap<Slot,Item> getEquipmentRaw() {
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
	
	public boolean canBattle(Playable other) {
		if(this instanceof Monster && other instanceof Monster && !Battle.prob(5)) return false;
		if(getGroup()!= null && other.getGroup()!= null && getGroup().equals(other.getGroup())) return false;
		
		return ( isWithinRange(other) );
	}
	
	private boolean hasGroup() {
		return getGroup() != null;
	}
	
	private boolean isWithinRange(Playable right) {
		
		if(hasGroup() && !right.hasGroup()) {
			if(calcTotalGroup() < right.calcTotalGroup()) return false;
			return !(calcTotalGroup()/BATTLE_MULTIPLIER > right.calcTotalGroup());	
			
		} else if(!hasGroup() && right.hasGroup()) {
			if(calcTotalGroup() > right.calcTotalGroup()) return false;
			return !(calcTotalGroup() < right.calcTotalGroup()/BATTLE_MULTIPLIER);	
		}
		
		//my attempt to make this more fair
		return (calcTotalGroup()/BATTLE_MULTIPLIER < right.calcTotalGroup() && 
				right.calcTotalGroup() < calcTotalGroup()*BATTLE_MULTIPLIER) 
				|| 
			   (calcTotalGroup()*BATTLE_MULTIPLIER > right.calcTotalGroup() && 
				right.calcTotalGroup() > calcTotalGroup()/BATTLE_MULTIPLIER);
	}
	
	private int calcTotalGroup() {
		if(getGroup() == null) return battleCheckTotal();
		int rev = 0;
		for(Playable p : getGroup()) {
			rev += p.calcTotal(null);
		}
		return rev;
	}
	
	public int battleCheckTotal() {
		int rev = 0;
		for(Item i : equipment.values()) {
			switch(i.getType()) {
			case Spiritual:
				rev += i.getValue()/1.5;
				break;
			case Emotional:
				rev += i.getValue()/1.1;
				break;
			case Physical:
			case Magical:
				rev += i.getValue();
				break;
			}
		}
		return rev;
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
			if(Math.random() > 0.5) 
				engage(p);
			else
				p.engage(this);
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