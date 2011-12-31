package listeners;

import java.util.Map.Entry;

import org.pircbotx.hooks.events.PrivateMessageEvent;

import data.DummyUsable;
import data.Item;
import data.Playable.Alignment;
import data.Playable.Slot;
import data.Player;
import data.Usable;

import bot.IdleBot;

public class CommandListener extends
		org.pircbotx.hooks.ListenerAdapter<IdleBot> {

	/*
	 * (non-Javadoc)
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onPrivateMessage(org.pircbotx.hooks.events.PrivateMessageEvent
	 * )
	 */
	@Override
	public void onPrivateMessage(PrivateMessageEvent<IdleBot> event)
			throws Exception {
		super.onPrivateMessage(event);

		String[] args = event.getMessage().split(" ");

		switch (args[0].toLowerCase()) {
		/**
		 * COMMAND: register
		 * ARGUMENTS: name password custom-class-name
		 * HELP: Register a new character.
		 * PENALTY: None.
		 */
		case "register":
			
			doRegister(event, args);
			break;

			/**
			 * COMMAND: login
			 * ARGUMENTS: name password
			 * HELP: Log in to a previously created character.
			 * PENALTY: None.
			 */
		case "login":
			
			doLogin(event, args);
			break;

			/**
			 * COMMAND: timeleft
			 * ARGUMENTS: none
			 * HELP: Get the remaining time to level.
			 * PENALTY: None.
			 */
		case "timeleft":
			doTimeleft(event);
			break;

			/**
			 * COMMAND: info
			 * ARGUMENTS: none
			 * HELP: Display all information about your character.
			 * PENALTY: None.
			 */
		case "info":
			doInfo(event, args);
			break;

			/**
			 * COMMAND: logout
			 * ARGUMENTS: none
			 * HELP: Safely log out of your character.
			 * PENALTY: None.
			 */
		case "logout":
			doLogout(event);
			break;

			/**
			 * COMMAND: online
			 * ARGUMENTS: none
			 * HELP: Show all currently logged in players.
			 * PENALTY: None.
			 */
		case "online":
			event.getBot().sendMessage(event.getUser(), event.getBot().getOnlinePlayers().toString());
			break;
			
			/**
			 * COMMAND: align
			 * ARGUMENTS: [Good|Neutral|Evil]
			 * HELP: Change alignment of your character.
			 * PENALTY: p30|p15|p5
			 */
		case "align":
			doAlignChange(event, args);
			break;

			/**
			 * COMMAND: ignore
			 * ARGUMENTS: none
			 * HELP: Ignore the bots notices about selling/trashing items.
			 * PENALTY: None.
			 */
		case "ignore":
			doIgnore(event);
			break;

			/**
			 * COMMAND: shop
			 * ARGUMENTS: [buy|info] itemname
			 * HELP: Buy or view items available for purchase.
			 * PENALTY: None.
			 */
		case "shop":
			doShop(event, args);
			break;

			/**
			 * COMMAND: use
			 * ARGUMENTS: itemname
			 * HELP: Use an item.
			 * PENALTY: None.
			 */
		case "use":
			doItemUse(event, args);
			break;

			/**
			 * COMMAND: class
			 * ARGUMENTS: new class name
			 * HELP: Change your class name.
			 * PENALTY: None.
			 */
		case "class":
			doClassChange(event, args);
			break;

			/**
			 * COMMAND: gold
			 * ARGUMENTS: None.
			 * HELP: Shows your current amount of gold.
			 * PENALTY: None.
			 */
		case "gold":
			doGold(event);
			break;
			
		case "reload":
			event.getBot().reload();
			break;
			
		case "cataclysm":
			//new Cataclysm();
			break;
			
		case "team":
			//new TeamEvent();
			break;
			
		case "announce":
			if(args.length < 2) return;
			event.getBot().messageChannel("[ANNOUNCEMENT] "+event.getMessage().substring(8));
			break;
			
		case "total":
			//event.getBot().sendMessage(event.getUser(), ""+IdleBot.botref.getPlayersRaw().size());
			break;
			
		case "levelup":
			//Player pl = event.getBot().getPlayerByUser(event.getUser().getNick());
			//pl.toNextLevel();
			break;
			
		case "warpall":
			//for(Player player : event.getBot().getPlayers()) {
			//	player.warp();
			//}
			break;
		/*	
		case "bossbattle":
			ArrayList<Player> left = new ArrayList<>(IdleBot.botref.getOnlinePlayers());
			ArrayList<Monster> right = new ArrayList<>();
			right.add(MonsterGenerator.generateMonster(Alignment.Evil, 1));
			new Battle(left, right);
			break;
			*/
			/*
		case "teambattle":
			ArrayList<Player> left = new ArrayList<>();
			ArrayList<Player> right = new ArrayList<>();
			LinkedList<Player> players = IdleBot.botref.getOnlinePlayers();
			if(players.size() < 2) return;
			Collections.shuffle(players);
			for(int i=0; i<players.size(); i++) {
				if(i%2 == 0) right.add(players.get(i));
				else left.add(players.get(i));
			}
			new Battle(left, right);
			break;*/

		default:
			event.getBot().sendMessage(event.getUser(), "Your command is invalid!");
			break;
		}
	}

	private void doGold(PrivateMessageEvent<IdleBot> event) {
		Player p = IdleBot.botref.getPlayerByUser(event.getUser());
		if(p == null) {
			event.getBot().sendMessage(event.getUser(), "You aren't logged in.");
			return;
		}
		event.getBot().sendMessage(event.getUser(), "Gold: "+p.getMoney());
	}

	private void doClassChange(PrivateMessageEvent<IdleBot> event, String[] args) {
		if(args.length<2) {
			event.getBot().sendMessage(event.getUser(), "Invalid arguments: class my-new-class");
			return;
		}
		Player p = IdleBot.botref.getPlayerByUser(event.getUser());
		if(p == null) {
			event.getBot().sendMessage(event.getUser(), "You aren't logged in.");
			return;
		}
		p.setClassType(event.getMessage().substring(event.getMessage().indexOf(args[1])));
		IdleBot.botref.messageChannel(p.getName() + " is now a/n: "+p.getClassType());
		IdleBot.botref.penalize(event.getUser(), p.getClassType().length());
	}

	private void doItemUse(PrivateMessageEvent<IdleBot> event, String[] args) {
		if(args.length < 2) {
			event.getBot().sendMessage(event.getUser(), "Invalid syntax: use [itemname]");
			return;
		}
		Player p = IdleBot.botref.getPlayerByUser(event.getUser());
		if(p == null) {
			event.getBot().sendMessage(event.getUser(), "That player does not exist!");
			return;
		}
		for(Usable u : p.getItems()) {
			if(u.getName().equals(args[1]) && u.getCount() > 0) {
				if(u.use(p)) {
					event.getBot().sendMessage(event.getUser(), "You used "+u.getName()+"!"+(u.getCount()-1 > 0  ?" You have "+(u.getCount()) + " left." : ""));
				}
				return;
			}
		}
		event.getBot().sendMessage(event.getUser(), "You don't have that item!");
	}

	private void doShop(PrivateMessageEvent<IdleBot> event, String[] args) {
		if(args.length < 2) {
			event.getBot().sendMessage(event.getUser(), "Invalid syntax: shop [buy|info] [itemname].");
			return;
		}
		switch(args[1]) {
		case "buy":
			if(args.length < 3) {
				event.getBot().sendMessage(event.getUser(), "Invalid syntax: shop buy [itemname].");
				return;
			}
			Player p = IdleBot.botref.getPlayerByUser(event.getUser());
			if(p == null) {
				event.getBot().sendMessage(event.getUser(), "That player does not exist!");
				return;
			}
			DummyUsable item = DummyUsable.findItem(args[2]);
			if(item == null) {
				event.getBot().sendMessage(event.getUser(), "That item does not exist!");
				return;
			}
			if(p.getMoney() >= item.cost) { 
				event.getBot().sendMessage(event.getUser(), "You bought "+args[2]+"! You have "+(p.getMoney()-item.cost)+" gold left.");
				p.addItem(new Usable(args[2]));
				p.setMoney(p.getMoney() - item.cost);
				if(item.name.equals("philosopherstone")) p.stats.hasPhilStone = true;
				if(item.name.equals("wingedshoes")) p.stats.hasWingShoes = true;
			} else {
				event.getBot().sendMessage(event.getUser(), "You don't have enough money! You need "+(item.cost - p.getMoney())+" more!");
			}
			break;
		case "info":
			if(args.length < 3) {
				event.getBot().sendMessage(event.getUser(), "Invalid syntax: shop info [itemname].");
				return;
			}
			DummyUsable itemName = DummyUsable.findItem(args[2]);
			if(itemName == null) {
				event.getBot().sendMessage(event.getUser(), "That item does not exist!");
				return;
			}
			event.getBot().sendMessage(event.getUser(), itemName.name+": "+itemName.desc);
			break;
		}
	}

	private void doAlignChange(PrivateMessageEvent<IdleBot> event, String[] args) {
		if(args.length < 2) {
			event.getBot().sendMessage(event.getUser(), "Invalid syntax: align [Good|Neutral|Evil].");
			return;
		}
		Player play = event.getBot().getPlayerByUser(event.getUser());
		if(play == null) {
			event.getBot().sendMessage(event.getUser(), "You aren't logged in!");
			return;
		}
		args[1] = args[1].toLowerCase();
		switch(args[1]) {
		case "good":
			play.setAlignment(Alignment.Good);
			IdleBot.botref.penalize(event.getUser(), 30);
			break;
		case "evil":
			play.setAlignment(Alignment.Evil);
			IdleBot.botref.penalize(event.getUser(), 5);
			break;
		case "neutral":
			play.setAlignment(Alignment.Neutral);
			IdleBot.botref.penalize(event.getUser(), 10);
			break;
		default:
			event.getBot().sendMessage(event.getUser(), "Invalid alignment: align [Good|Neutral|Evil].");
			return;
		}
		event.getBot().sendMessage(event.getUser(), "You are now "+args[1] + " aligned!");
		event.getBot().messageChannel(play.getName() + " changed alignment to "+args[1] + "!");
	}

	private void doIgnore(PrivateMessageEvent<IdleBot> event) {
		Player p = event.getBot().getPlayerByUser(event.getUser());
		if(p == null) return;
		if(p.isIgnoring) { 
			event.getBot().sendMessage(event.getUser(), "You are no longer ignoring my messages.");
		} else {
			event.getBot().sendMessage(event.getUser(), "You are now ignoring my messages.");
		}
		p.isIgnoring = !p.isIgnoring;
	}

	private void doTimeleft(PrivateMessageEvent<IdleBot> event) {
		Player p = event.getBot().getPlayerByUser(event.getUser().getNick());
		if(p == null) {
			event.getBot().sendMessage(event.getUser(), "You are already logged in. Cheater.");
			return;
		}
		event.getBot().sendMessage(event.getUser(), "You have "+event.getBot().ms2dd(p.getTimeLeft()) + " to next level.");
	}

	private void doInfo(PrivateMessageEvent<IdleBot> event, String[] args)
			throws InterruptedException {
		String snick;
		if(args.length > 1) { snick = args[1]; }
		else snick = event.getUser().getNick();
		Player play = event.getBot().getPlayerByUser(snick);
		if(play == null) { 
			event.getBot().sendMessage(event.getUser(), "That user doesn't exist.");
			return;
		}
		if(play.loggedIn) {
			event.getBot().sendMessage(event.getUser(), play.toString());
			event.getBot().sendMessage(event.getUser(), "You have "+play.getMoney() + " gold.");
			event.getBot().sendMessage(event.getUser(), "You have "+event.getBot().ms2dd(play.getTimeLeft()) + " to next level.");
			event.getBot().sendMessage(event.getUser(), "Equipment <<"+play.calcTotal(null)+">>");
			for(Entry<Slot, Item> i : play.getEquipment()) {
				event.getBot().sendMessage(event.getUser(), i.getKey()+": "+i.getValue() + " <<"+i.getValue().getValue()+">>");
				Thread.sleep(300);
			}
		} else 
			event.getBot().sendMessage(event.getUser(), "You aren't even logged in.");
	}

	private void doLogout(PrivateMessageEvent<IdleBot> event) {
		if(!event.getBot().findLoggedInUser(event.getUser().getNick())) {
			event.getBot().sendMessage(event.getUser(), "You aren't logged in.");
			return;
		}

		event.getBot().sendMessage(event.getUser(), "You have been successfully logged out.");

		Player p = IdleBot.botref.getPlayerByUser(event.getUser());
		p.lastLogin = null;
		
		event.getBot().handleLogout(event.getUser());
	}

	private void doLogin(PrivateMessageEvent<IdleBot> event, String[] args) {
		if(args.length < 3) {
			event.getBot().sendMessage(event.getUser(), "You didn't tell me enough information.");
			return;
		}
		
		if(!event.getBot().getGlobalChannel().getUsers().contains(event.getUser())){
			event.getBot().sendMessage(event.getUser(), "You aren't even in the channel.");
			return;
		}
		
		String name = args[1];
		String password = args[2];
		
		if(event.getBot().findLoggedInUser(event.getUser().getNick())) {
			event.getBot().sendMessage(event.getUser(), "You are already logged in. Cheater.");
			return;
		}
		
		Player player = event.getBot().findPlayer(name);
		
		if(player != null) {
			
			if(player.loggedIn) {
				event.getBot().sendMessage(event.getUser(), "You are already logged in. Cheater.");
				return;
			}
			
			if(player.login(password)) {
				event.getBot().sendMessage(event.getUser(), "You are successfully logged in as "+player+". "+event.getBot().ms2dd(player.getTimeLeft()) + " to next level.");
				event.getBot().handleLogin(event.getUser(), player);
			} else {
				event.getBot().sendMessage(event.getUser(), "Your login attempt failed. Stop hacking.");
			}
		} else {
			event.getBot().sendMessage(event.getUser(), "Your login attempt failed. Stop hacking.");
		}
	}

	private void doRegister(PrivateMessageEvent<IdleBot> event, String[] args) {
		//TODO make classes support more  than one word
		if(args.length < 4) {
			event.getBot().sendMessage(event.getUser(), "You didn't tell me enough information.");
			return;
		}
		
		String name = args[1];
		String password = args[2];
		String cclass = args[3];
		
		if(event.getBot().findPlayer(name) != null) {
			event.getBot().sendMessage(event.getUser(), "That guy already registered.");
			return;
		}
		
		event.getBot().createPlayer(event.getUser(), name, password, cclass);
		
		event.getBot().sendMessage(event.getUser(), "You have registered the character \""+name+"\".");
	}

}
