package Actions;

import android.app.Notification;

import com.example.joecollenette.legorovers.BluetoothRobot;

/**
 * Created by joecollenette on 08/07/2015.
 */
public class ActionListItem
{
	private BluetoothRobot.RobotActions action;

	public ActionListItem(BluetoothRobot.RobotActions _action)
	{
		action = _action;
	}
}
