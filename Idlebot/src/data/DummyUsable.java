package data;

public class DummyUsable {
	public int cost;
	public String name;
	public String desc;
	public boolean buyable=true;
	public boolean findable;
	
	public DummyUsable(String name, int cost, String desc, boolean buyable, boolean findable) {
		this.cost = cost;
		this.name = name;
		this.desc = desc;
		this.buyable = buyable;
		this.findable = findable;
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
		new DummyUsable("fortunecookie", 20000, "Everyone loves to eat these tasty treats!", true, true),
		new DummyUsable("karmacookie", 	30000, "Mmmm, tastes like revenge!", false, true),
		new DummyUsable("sledgehammer",45000, "Smashy smashy!", true, true),
		new DummyUsable("minuteglass", 	500, "The Sands of Time: every flip of the glass is a minute.", true, true),
		new DummyUsable("hourglass", 		10000, "The Sands of Time: every flip of the glass is an hour.", true, true),
		new DummyUsable("dayglass", 			95000, "The Sands of Time: every flip of the glass is a day.", true, true),
		new DummyUsable("weekglass", 		350000, "The Sands of Time: every flip of the glass is a week.", true, false),
		new DummyUsable("lifeglass", 			1100000, "The Sands of Time: every flip of the glass is a lifetime.", true, false),
		new DummyUsable("crystalshard", 	500, "A shiny, translucent crystal with a glowing purple center.", true, true),
		new DummyUsable("duelshard", 		85000, "A silver, translucent crystal shaped like a sword.", true, true),
		new DummyUsable("fightshard", 		35000, "A shiny, translucent crystal with a glowing red center.", false, true),
		new DummyUsable("goldshard", 		146500, "A miniature chunk of a well-known gold cluster.", false, true),
		new DummyUsable("starshard", 		455000, "A small, star-shaped crystal.", false, true),
		new DummyUsable("crystalball", 		235000, "A translucent orb carved out of a solid crystal, with a swirling, stormy core.", true, true),
		new DummyUsable("boo", 				65000, "A mean ghost.", true, false),
		new DummyUsable("megaboo", 		110000, "A really mean ghost.", true, false),
		new DummyUsable("pocketwatch", 	10000, "A small pocketwatch.", true, true),
		new DummyUsable("bomb", 				50000, "A dangerous explosive.", true, true),
		new DummyUsable("mirv", 				250000, "A really dangerous explosive.", true, false),
		new DummyUsable("lottoticket", 		6000, "A ticket to your dreamland.", false, true),
		new DummyUsable("luckydice", 		16000, "A pair of dice once used by a famous gambler.", false, true),
		new DummyUsable("pandorasbox", 	750000, "A black box from the age of the ancients.", true, false),
		new DummyUsable("wishingwell", 	46000, "A giant well. How are you supposed to carry this around?", true, false),
		new DummyUsable("wishingfountain", 	456000, "An enormous fountain. Do you have a packmule?", true, false),
		new DummyUsable("mirror", 			10000, "A mirror that seems to reflect light. Who'd've thought that was possible?", true, true),
		new DummyUsable("darkmirror", 		100000, "A mirror that seems to reflect the darkest desires within you.", true, false),
		new DummyUsable("genielamp", 		60000, "A solid gold lamp.", true, true),
		new DummyUsable("geniilamp", 		20000, "A solid bronze lamp.", true, true),
		new DummyUsable("abacus", 			100000, "What happens next is anyone's guess!", true, true),
		new DummyUsable("flagofvalor", 		35000, "This flag incites valor in all who see it!", true, true),
		new DummyUsable("philosopherstone", 450000, "A small, chromatic gem.", true, false),
		new DummyUsable("wingedshoes", 	175000, "A pair of shoes with wings attached to them. Nothing special.", true, false),
	};
	
}
