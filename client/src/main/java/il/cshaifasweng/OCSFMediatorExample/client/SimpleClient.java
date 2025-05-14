package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleClient extends AbstractClient {

	private int player;
	private static SimpleClient client = null;
	String mark;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	public static void initialize(String host, int port) {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else{
			EventBus.getDefault().post(msg);
		}
	}

	public static SimpleClient getClient() {
		return client;
	}
}