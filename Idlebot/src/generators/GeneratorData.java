package generators;

import generators.Utilities.Data;

import java.net.MalformedURLException;
import java.net.URL;

public class GeneratorData {
	public static Data[] colors;
	public static Data[] downvalue;
	public static Data[] upvalue;
	public static Data[] evils;
	public static Data[] goods;
	public static Data[] neutrals;
	public static Data[] prefixes;

	public static final Data[] DUMMYARRAY = new Data[0];

	static {
		try {
			loadOthers();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadOthers() throws MalformedURLException {
		colors = Utilities
				.loadFile(
						new URL(
								"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/Colors.txt"))
				.toArray(DUMMYARRAY);
		downvalue = Utilities.loadFile(
				new URL(
						"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/Devalued.txt")).toArray(DUMMYARRAY);
		upvalue = Utilities.loadFile(
				new URL(
						"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/Valuable.txt")).toArray(DUMMYARRAY);
		goods = Utilities
				.loadFile(new URL(
						"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/Goods.txt"))
				.toArray(DUMMYARRAY);
		evils = Utilities
				.loadFile(new URL(
						"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/Evils.txt"))
				.toArray(DUMMYARRAY);
		neutrals = Utilities.loadFile(
				new URL(
						"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/Neutrals.txt")).toArray(DUMMYARRAY);
		prefixes = Utilities.loadFile(
				new URL(
						"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/Prefixes.txt")).toArray(DUMMYARRAY);
	}
	
	private static Data getRandom(Data[] array) {
		return array[(int) (Math.random() * (array.length - 1))];
	}
	
	public static Data getRColor() {
		return getRandom(colors);
	}
	
	public static Data getRGAlign() {
		return getRandom(goods);
	}
	
	public static Data getREAlign() {
		return getRandom(evils);
	}
	
	public static Data getRNAlign() {
		return getRandom(neutrals);
	}
	
	public static Data getRPrefix() {
		return getRandom(prefixes);
	}

	public static Data getRUpvalue() {
		return getRandom(upvalue);
	}
	
	public static Data getRDownvalue() {
		return getRandom(downvalue);
	}
	
}