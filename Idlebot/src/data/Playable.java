package data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.pircbotx.Colors;

import data.Item.Type;

import events.Battle;

import bot.IdleBot;

public abstract class Playable {
	
	public static final int MAX_X = 150;
	public static final int MAX_Y = 150;
	
	public static final double BATTLE_MULTIPLIER = 2.5;
	
	public enum Direction { NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST };
	public enum Alignment { Evil, Good, Neutral };
	public enum Slot { Neck, Head, Charm, Hands, Feet, Legs, Finger, Shield, Body, Weapon };

	protected Alignment alignment;
	
	public transient int health = 0;
	
	protected String classType;

	protected HashMap<Slot, Item> equipment = new HashMap<>();

	protected short level = 0;

	protected String name;

	protected int x = 0;

	protected int y = 0;

	public Playable(String name2, String classtype2, Alignment align) {
		this.name = name2;
		this.classType = classtype2;
		this.alignment = align;
	}
	/**
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}
	/**
	 * @return the classType
	 */
	public String getClassType() {
		return classType;
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
	 * @param alignment the alignment to set
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	/**
	 * @return the equipment
	 */
	public Set<Entry<Slot, Item>> getEquipment() {
		return equipment.entrySet();
	}
	/**
	 * @param classType the classType to set
	 */
	public void setClassType(String classType) {
		this.classType = classType;
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

	public int calcTotal(data.Item.Type type) {
		int rev = 0;
		for(Item i : equipment.values()) {
			if(type!=null && i.getType() == type || type == null) rev += i.getValue();
		}
		return rev;
	}
	
	public void takeTurn() {
		move();
	}
	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	public void move(int xoff, int yoff) {
		x += xoff;
		y += yoff;
	}
	
	public void move() {
		if(x<0 || x>MAX_X) x=0;
		if(y<0 || y>MAX_Y) y=0;
		switch(Direction.values()[(int) (Math.random() * Direction.values().length)]) {
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
	private boolean underX() {
		return x+1 < MAX_X;
	}
	private boolean aboveY() {
		return y-1 > 0;
	}
	private boolean aboveX() {
		return x-1 > 0;
	}
	private boolean underY() {
		return y+1 < MAX_Y;
	}
	
	public void dropItem() {
		
	}
	
	public void engage(Playable other) {
		if(canBattle(this, other)) {
			new Battle(this, other);
		} else {
			if(this.level > other.level + 3 || this.calcTotal(null) > other.calcTotal(null) * 2) {
				IdleBot.botref.messageChannel(Battle.BATTLE + getName() + " walked past "+other.getName()+", laughing so hard, s/he was crying.");
			} else if(this.level < other.level - 3 || this.calcTotal(null) * 2 < other.calcTotal(null)) {
				IdleBot.botref.messageChannel(Battle.BATTLE + other.getName() + " passed by "+getName()+", laughing and gloating.");
			} else {
				IdleBot.botref.messageChannel(Battle.BATTLE + other.getName() + " and "+getName() + " wave as they pass by each other.");
			}
		}
		warp();
	}
	
	private boolean canBattle(Playable playable, Playable other) {
		return ( !isWithinLevel(playable, other) && !isWithinRange(playable, other));
	}
	
	private boolean isWithinRange(Playable left, Playable right) {
		return (left.calcTotal(null)*BATTLE_MULTIPLIER < right.calcTotal(null) || right.calcTotal(null)*BATTLE_MULTIPLIER < left.calcTotal(null));
	}
	
	private boolean isWithinLevel(Playable left, Playable right) {
		return (left.level*BATTLE_MULTIPLIER < right.level || right.level*BATTLE_MULTIPLIER < left.level);
	}
	
	public String getBattleName() {
		return Colors.BOLD + getName() + Colors.NORMAL;
	}
	
	public String toBattleString() {
		return getName() + Colors.BOLD +" ("+Colors.RED+health+Colors.NORMAL+"/"+Colors.RED+calcTotal(null)+Colors.NORMAL+") "+getColorNumber(Colors.BROWN, Type.Physical)+getColorNumber(Colors.BLUE, Type.Magical)+getColorNumber(Colors.PURPLE, Type.Emotional)+getColorNumber(Colors.DARK_BLUE, Type.Spiritual);
	}
	
	public String getColorNumber(String color, Type type) {
		return calcTotal(type) == 0 ? "" : "["+color+calcTotal(type)+Colors.NORMAL+"] ";
	}
	
	public void warp() {
		x = (int) (Math.random() * MAX_X);
		y = (int) (Math.random() * MAX_Y);
	}
}