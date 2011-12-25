package data;

import org.pircbotx.UserSnapshot;

public class UserData implements Comparable<UserData>{
	
	private String realName, hostMask, login, nick;
	
	public UserData(UserSnapshot user) {
		this.setRealName(user.getRealName());
		this.hostMask = user.getHostmask();
		this.login = user.getLogin();
		this.nick = user.getNick();
	}

	@Override
	public int compareTo(UserData o) {
		return o.getNick().compareToIgnoreCase(nick);
	}

	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * @param realName the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/**
	 * @return the hostMask
	 */
	public String getHostMask() {
		return hostMask;
	}

	/**
	 * @param hostMask the hostMask to set
	 */
	public void setHostMask(String hostMask) {
		this.hostMask = hostMask;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * @param nick the nick to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}
	
}
