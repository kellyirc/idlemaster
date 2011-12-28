package listeners;

import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

import data.Player;
import data.UserData;

import bot.IdleBot;

public class UserListener extends org.pircbotx.hooks.ListenerAdapter<IdleBot> {

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onJoin(org.pircbotx.hooks.events.JoinEvent)
	 */
	@Override
	public void onJoin(JoinEvent<IdleBot> event) throws Exception {
		super.onJoin(event);
		Thread.sleep(6000);
		if (event.getUser().equals(event.getBot().getUserBot())) {
			for (User u : event.getChannel().getUsers()) {
				if(!u.equals(event.getBot().getUserBot())) tryToMatch(u);
			}
		} else {
			tryToMatch(event.getUser());
		}
	}
	
	private void tryToMatch(User u) {
		for (Player p : IdleBot.botref.getPlayers()) {
			for (UserData alias : p.getAliases()) {
				if ((alias.getLogin().equals(u.getLogin()) && alias.getRealName().equals(u.getRealName()) || alias
								.getHostMask().equals(u.getHostmask())) && p.lastLogin!= null && p.lastLogin.compareTo(alias) == 0 && !IdleBot.botref.findLoggedInUser(u.getNick())) {
					IdleBot.botref.handleLogin(u, p);
					break;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onKick(org.pircbotx.hooks.events.KickEvent)
	 */
	@Override
	public void onKick(KickEvent<IdleBot> event) throws Exception {
		super.onKick(event);
		IdleBot.botref.handleLogout(event.getRecipient());
	}

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onPart(org.pircbotx.hooks.events.PartEvent)
	 */
	@Override
	public void onPart(PartEvent<IdleBot> event) throws Exception {
		super.onPart(event);
		IdleBot.botref.handleLogout(event.getUser());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onNickChange(org.pircbotx.hooks.events.NickChangeEvent)
	 */
	@Override
	public void onNickChange(NickChangeEvent<IdleBot> event) throws Exception {

		super.onNickChange(event);

		IdleBot.botref.movePlayerToNewNick(event.getOldNick(),
				event.getNewNick());
	}

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onQuit(org.pircbotx.hooks.events.QuitEvent)
	 */
	@Override
	public void onQuit(QuitEvent<IdleBot> event) throws Exception {
		super.onQuit(event);
		IdleBot.botref.handleLogout(event.getUser());
	}

}
