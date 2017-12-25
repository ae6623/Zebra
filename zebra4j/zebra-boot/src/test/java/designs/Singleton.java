package designs;

/**
 * Created by lzy@js-dev.cn on 2017/1/15 0015.
 */
public class Singleton {
 	private static class SingletonInstace {
		public static Singleton instance = new Singleton();
	}

	private Singleton(){

	}

	public Singleton getInstance() {
		return SingletonInstace.instance;
	}
}
