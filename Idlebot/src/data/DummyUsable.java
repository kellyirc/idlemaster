package data;

public class DummyUsable {
	public int cost;
	public String name;
	public String desc;
	
	public DummyUsable(String name, int cost, String desc) {
		this.cost = cost;
		this.name = name;
		this.desc = desc;
	}
	
	public static DummyUsable findItem(String s) {
		for(DummyUsable du : dummyItems) {
			if(du.name.equals(s)) {
				return du;
			}
		}
		return null;
	}
	
	public static DummyUsable[] dummyItems = {
		new DummyUsable("fortunecookie", 20000, "Everyone loves to eat these tasty treats!"),
		new DummyUsable("karmacookie", 	30000, "Mmmm, tastes like revenge!"),
		new DummyUsable("sledgehammer",45000, "Smashy smashy!"),
		new DummyUsable("minuteglass", 	500, "The Sands of Time: every flip of the glass is a minute."),
		new DummyUsable("hourglass", 		10000, "The Sands of Time: every flip of the glass is an hour."),
		new DummyUsable("dayglass", 			95000, "The Sands of Time: every flip of the glass is a day."),
		new DummyUsable("weekglass", 		350000, "The Sands of Time: every flip of the glass is a week."),
		new DummyUsable("lifeglass", 			1100000, "The Sands of Time: every flip of the glass is a lifetime."),
		new DummyUsable("crystalshard", 	500, "A shiny, translucent crystal with a glowing purple center."),
		new DummyUsable("duelshard", 		85000, "A silver, translucent crystal shaped like a sword."),
		new DummyUsable("fightshard", 		35000, "A shiny, translucent crystal with a glowing red center."),
		new DummyUsable("goldshard", 		146500, "A miniature chunk of a well-known gold cluster."),
		new DummyUsable("starshard", 		255000, "A small, star-shaped crystal."),
		new DummyUsable("crystalball", 		185000, "A translucent orb carved out of a solid crystal, with a swirling, stormy core."),
		new DummyUsable("boo", 				65000, "A mean ghost."),
		new DummyUsable("megaboo", 		110000, "A really mean ghost."),
		new DummyUsable("pocketwatch", 	10000, "A small pocketwatch."),
		new DummyUsable("bomb", 				50000, "A dangerous explosive."),
		new DummyUsable("mirv", 				250000, "A really dangerous explosive."),
		new DummyUsable("lottoticket", 		6000, "A ticket to your dreamland."),
		new DummyUsable("luckydice", 		16000, "A pair of dice once used by a famous gambler."),
		new DummyUsable("pandorasbox", 	750000, "A black box from the age of the ancients."),
		new DummyUsable("wishingwell", 	46000, "A giant well. How are you supposed to carry this around?"),
		new DummyUsable("wishingfountain", 	456000, "An enormous fountain. Do you have a packmule?"),
		new DummyUsable("mirror", 			10000, "A mirror that seems to reflect light. Who'd've thought that was possible?"),
		new DummyUsable("darkmirror", 		100000, "A mirror that seems to reflect the darkest desires within you."),
		new DummyUsable("genielamp", 		60000, "A solid gold lamp."),
		new DummyUsable("geniilamp", 		20000, "A solid bronze lamp."),
		new DummyUsable("philosopherstone", 450000, "A small, chromatic gem."),
		new DummyUsable("wingedshoes", 	175000, "A pair of shoes with wings attached to them. Nothing special."),
	};
	
}
