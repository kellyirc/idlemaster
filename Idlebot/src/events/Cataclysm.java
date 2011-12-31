package events;

import org.pircbotx.Colors;

import data.Playable;
import data.Player;
import bot.IdleBot;

//TODO cataclysm that hits offline players
//TODO make cataclysms an enum and able to cause specific ones to happen
public class Cataclysm {

	public enum Quadrant {
		I, II, III, IV
	}

	public static final int MONEY_GAIN = 5000;

	public Cataclysm() {
		Quadrant quad = Quadrant.values()[(int) (Math.random() * Quadrant
				.values().length)];
		int count = 0;
		switch ((int) (Math.random() * 13)) {

		case 0:
			IdleBot.botref
					.messageChannel(Colors.DARK_GRAY
							+ "The sky is darkening, the earth is rising, and everything appears to be ending..");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;

				int perc = (int) ((Math.random() * 20) + 1);
				long timeGain = (long) (((Math.pow(1.16, p.getLevel())) * 3250000) * ((double) perc / 100));
				IdleBot.botref.messageChannel(Colors.DARK_GRAY + p.getName()
						+ " had " + IdleBot.botref.ms2dd(timeGain)
						+ " added to his/her level timer!");
				p.modifyTime(-timeGain);

				int moneyLoss = (int) (p.getMoney() * (MoneyEvent.percent / 100.0));
				IdleBot.botref.messageChannel(Colors.DARK_GRAY + p.getName()
						+ " has lost " + moneyLoss + " gold!");
				p.setMoney(p.getMoney() - moneyLoss);
			}
			break;

		case 1:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "The sky shines brightly in Quadrant " + quad + "!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				if (this.isInQuadrant(p, quad)) {
					p.stats.cataSuffered++;
					count++;

					int perc = (int) (Math.random() * 17) + 1;
					long timeGain = (long) (((Math.pow(1.16, p.getLevel())) * 1250000) * ((double) perc / 100));
					IdleBot.botref.messageChannel(Colors.DARK_GRAY
							+ p.getName() + " had "
							+ IdleBot.botref.ms2dd(timeGain)
							+ " taken away from his/her level timer!");
					p.modifyTime(timeGain);

				}
			}
			if (count == 0)
				IdleBot.botref.messageChannel(Colors.DARK_GRAY
						+ "... but no one was around to enjoy it!");
			break;

		case 2:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "The sky glows scornfully in Quadrant " + quad + "!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				if (this.isInQuadrant(p, quad)) {
					p.stats.cataSuffered++;
					count++;

					int perc = (int) (Math.random() * 31) + 1;
					long timeGain = (long) (((Math.pow(1.16, p.getLevel())) * 750000) * ((double) perc / 100));
					IdleBot.botref.messageChannel(Colors.DARK_GRAY
							+ p.getName() + " had "
							+ IdleBot.botref.ms2dd(timeGain)
							+ " added to his/her level timer!");
					p.modifyTime(-timeGain);
				}
			}
			if (count == 0)
				IdleBot.botref.messageChannel(Colors.DARK_GRAY
						+ "... but luckily no one was there!");
			break;

		case 3:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "A cyclone has appeared in Quadrant " + quad + "!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				if (this.isInQuadrant(p, quad)) {
					p.stats.cataSuffered++;
					count++;

					int perc = (int) (Math.random() * 15) + 1;
					long timeGain = (long) (((Math.pow(1.16, p.getLevel())) * 305000) * ((double) perc / 100));
					IdleBot.botref.messageChannel(Colors.DARK_GRAY
							+ p.getName() + " had "
							+ IdleBot.botref.ms2dd(timeGain)
							+ " added to his/her level timer!");
					p.modifyTime(-timeGain);
					p.warp();
				}
			}
			if (count == 0)
				IdleBot.botref.messageChannel(Colors.DARK_GRAY
						+ "... but luckily no one was there!");
			break;

		case 4:
			IdleBot.botref
					.messageChannel(Colors.DARK_GRAY
							+ "The deities breathe mercy into the world! Everyones time to level has been reduced!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;
				long timeGain = (long) (((Math.pow(1.16, p.getLevel())) * 225000) * 0.357);
				IdleBot.botref.messageChannel(Colors.DARK_GRAY + p.getName()
						+ " had " + IdleBot.botref.ms2dd(timeGain)
						+ " taken away from his/her level timer!");
				p.modifyTime(timeGain);
			}
			break;

		case 5:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "A Void Crystal has appeared!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;

				int perc = (int) (Math.random() * 5) + 1;
				long timeGain = (long) (((Math.pow(1.16, p.getLevel())) * 1000000) * ((double) perc / 100));
				IdleBot.botref.messageChannel(Colors.DARK_GRAY + p.getName()
						+ " had " + IdleBot.botref.ms2dd(timeGain)
						+ " added to his/her level timer!");
				p.modifyTime(-timeGain);
				p.warp();
			}
			break;

		case 6:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "A Planar Shift has occurred!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;
				p.warp();
			}
			break;

		case 7:
			IdleBot.botref
					.messageChannel(Colors.DARK_GRAY
							+ "The deities have smiled upon this world! The sky begins to rain gold! [+"
							+ MONEY_GAIN + "]");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;
				p.setMoney(p.getMoney() + MONEY_GAIN);
			}
			break;

		case 8:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "An earthquake is tearing up Quadrant " + quad + "!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				if (this.isInQuadrant(p, quad)) {
					p.stats.cataSuffered++;
					count++;

					int perc = (int) ((Math.random() * 20) + 1);
					long timeGain = (long) (((Math.pow(1.16, p.getLevel())) * 3000000) * ((double) perc / 100));
					IdleBot.botref.messageChannel(Colors.DARK_GRAY
							+ p.getName() + " had "
							+ IdleBot.botref.ms2dd(timeGain)
							+ " added to his/her level timer!");
					p.modifyTime(-timeGain);

					int moneyLoss = (int) (p.getMoney() * ((MoneyEvent.percent * 5) / 100.0));
					IdleBot.botref
							.messageChannel(Colors.DARK_GRAY + p.getName()
									+ " has lost " + moneyLoss + " gold!");
					p.setMoney(p.getMoney() - moneyLoss);
				}
			}
			if (count == 0)
				IdleBot.botref.messageChannel(Colors.DARK_GRAY
						+ "... but luckily no one was there!");
			break;

		case 9:
			Player rand = IdleBot.botref.getRandomPlayer();
			IdleBot.botref
					.messageChannel(Colors.DARK_GRAY
							+ rand.getName()
							+ " has become a very massive object, pulling all players to him/her!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;
				p.warp(rand);
			}
			break;

		case 10:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "Everyone's in for a surprise!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;
				new TimeEvent(p, TimeEvent.Type.Fatehand);
			}
			break;

		case 11:
			IdleBot.botref
					.messageChannel(Colors.DARK_GRAY
							+ "It seems as though the last ray of light has left this world...");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;
				new TimeEvent(p, TimeEvent.Type.Forsaken);
				new ItemEvent(p, false);
				new MoneyEvent(p, false);
			}
			break;

		case 12:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "There is yet hope in the world!");
			for (Player p : IdleBot.botref.getOnlinePlayers()) {
				p.stats.cataSuffered++;
				new TimeEvent(p, TimeEvent.Type.Blessing);
				new ItemEvent(p, true);
				new MoneyEvent(p, true);
			}
			break;
			
		case 13:
			IdleBot.botref.messageChannel(Colors.DARK_GRAY
					+ "Rays of black pour down from the sky!");
			IdleBot.botref.generateMonsters();
			break;
		}
	}

	private boolean isInQuadrant(Player p, Quadrant q) {
		switch (q) {
		case I:
			if (p.getX() > Playable.MAX_X / 2 && p.getY() > Playable.MAX_Y / 2)
				return true;
			return false;
		case II:
			if (p.getX() < Playable.MAX_X / 2 && p.getY() > Playable.MAX_Y / 2)
				return true;
			return false;
		case III:
			if (p.getX() < Playable.MAX_X / 2 && p.getY() < Playable.MAX_Y / 2)
				return true;
			return false;
		case IV:
			if (p.getX() > Playable.MAX_X / 2 && p.getY() < Playable.MAX_Y / 2)
				return true;
			return false;
		}
		return false;
	}
}
