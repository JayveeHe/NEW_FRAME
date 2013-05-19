package fyy.ygame_map.logic;

public class BuffInfo {
	private int damage = 0;
	private boolean isLasting = false;// ÊÇ·ñÎª³£×¤BUFF
	private int lasting_count = 0;
	private boolean isDiren = false;

	public BuffInfo() {
		this.setDamage(0);
		this.setLasting(false);
		this.setLasting_count(0);
	}

	public int getLasting_count() {
		return lasting_count;
	}

	public void setLasting_count(int lasting_count) {
		this.lasting_count = lasting_count;
	}

	public boolean isLasting() {
		return isLasting;
	}

	public void setLasting(boolean isLasting) {
		this.isLasting = isLasting;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

}
