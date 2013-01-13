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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostMask == null) ? 0 : hostMask.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((nick == null) ? 0 : nick.hashCode());
		result = prime * result
				+ ((realName == null) ? 0 : realName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserData other = (UserData) obj;
		if (hostMask == null) {
			if (other.hostMask != null)
				return false;
		} else if (!hostMask.equals(other.hostMask))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (nick == null) {
			if (other.nick != null)
				return false;
		} else if (!nick.equals(other.nick))
			return false;
		if (realName == null) {
			if (other.realName != null)
				return false;
		} else if (!realName.equals(other.realName))
			return false;
		return true;
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
