package listeners;

import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;

import data.IdleMaster;
import data.Player;

import bot.Globals;
import bot.IdleBot;

public class SaveListener extends org.pircbotx.hooks.ListenerAdapter<IdleBot> {

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onConnect(org.pircbotx.hooks.events.ConnectEvent)
	 */
	@Override
	public void onConnect(ConnectEvent<IdleBot> event) throws Exception {
		IdleBot.botref.loadPlayers();
		if (IdleBot.botref.findPlayer("IdleMaster") == null) {

			IdleMaster i = new IdleMaster();

			IdleBot.botref.getPlayersRaw().add(i);

			IdleBot.botref.savePlayers(false);
			
			i.loggedIn = true;

			i.fromSerialize();
		} else {
			Player p = IdleBot.botref.findPlayer("IdleMaster");
			IdleBot.botref.handleLogin(event.getBot().getUserBot(), p);
		}
		super.onConnect(event);
		
		IdleBot.botref.generateMonsters();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onDisconnect(org.pircbotx.hooks.events.DisconnectEvent)
	 */
	@Override
	public void onDisconnect(DisconnectEvent<IdleBot> event) throws Exception {
		IdleBot.botref.savePlayers(false);
		super.onDisconnect(event);
		for(Player p : event.getBot().getPlayers()) {
			if(p.loggedIn) event.getBot().handleLogout(p);
		}
		while(!event.getBot().isConnected()){
			System.err.println("Reconnecting attempt..");
			event.getBot().reconnect();
			Thread.sleep(10000);
		}
		event.getBot().joinChannel(Globals.Channel);
	}

}
