package EV3;

import android.graphics.PointF;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by joecollenette on 02/07/2015.
 */
public class BluetoothRobot implements Runnable
{
	public enum ConnectStatus {CONNECTED, DISCONNECTED, CONNECTING, DISCONNECTING};

	public enum RobotActions
	{
		NOTHING(0),
		FORWARD(1),
		FORWARD_A_BIT(2),
		STOP(3),
		BACK_A_BIT(4),
		BACKWORD(5),
		LEFT(6),
		LEFT_A_BIT(7),
		RIGHT(8),
		RIGHT_A_BIT(9),
		SCARE(10);

		int value;
		RobotActions(int i)
		{
			value = i;
		}

		public int toInt()
		{
			return value;
		}

		public static RobotActions fromInt(int i)
		{
			for (int j = 0; j < a.length; j++)
			{
				if (a[j].toInt() == i)
				{
					return a[j];
				}
			}
			return NOTHING;
		}
	}

	private static RobotActions[] a = RobotActions.values();

	public class RobotRules
	{
		private boolean on;
		private RobotActions[] actions;
		private int onAppeared;

		public RobotRules()
		{
			on = false;
			onAppeared = 0;
			actions = new RobotActions[]{RobotActions.NOTHING, RobotActions.NOTHING, RobotActions.NOTHING};
		}

		public void setEnabled(boolean enabled)
		{
			on = enabled;
		}

		public boolean getEnabled()
		{
			return on;
		}

		public void setOnAppeared(int appear)
		{
			onAppeared = appear;
		}

		public int getOnAppeared()
		{
			return onAppeared;
		}

		public void editAction(RobotActions action, int pos)
		{
			actions[pos] = action;
		}

		public RobotActions getAction(int pos)
		{
			return actions[pos];
		}
	}

    private Robot robot;
    private Exception generatedException;
	private String btAddress;
	private LinkedBlockingDeque<RobotActions> actions;
	private ConnectStatus status = ConnectStatus.DISCONNECTED;

	private String distance = "Distance is -";
	private String light = "Light is -";
	private RobotRules[] rules;

	private boolean path = false;
	private boolean water = false;
	private boolean obstacle = false;
	private boolean obstacleChanged;
	private StringBuilder beliefs;

	private float objectDetected = 0.4f;
	private float pathLight = 0.09f;
	private PointF waterLightRange = new PointF(0.06f, 0.09f);

	private void updateBeliefs(float distance, float light)
	{
		obstacleChanged = obstacle != (distance < objectDetected);
		obstacle = Float.compare(distance, objectDetected) < 0;

		path = Float.compare(light, pathLight) > 0;
		water = (Float.compare(light, waterLightRange.x) > 0) && (Float.compare(light, waterLightRange.y) < 0);

		beliefs.setLength(0);
		beliefs.append("Beliefs - [");
		if (obstacle)
		{
			beliefs.append("Obstacle, ");
		}
		if (water)
		{
			beliefs.append("Water, ");
		}
		if (path)
		{
			beliefs.append("Path");
		}

		if (beliefs.toString().endsWith(", "))
		{
			beliefs.setLength(beliefs.length() - 2);
		}
		beliefs.append(']');
	}

	private void checkRules()
	{
		for (int i = 0; i < rules.length; i++)
		{
			RobotRules rule = rules[i];
			boolean doActions = false;
			if (rule.getEnabled())
			{
				switch (rule.getOnAppeared())
				{
					case 0:
						doActions = obstacleChanged && obstacle;
						break;
					case 1:
						doActions = obstacleChanged && !obstacle;
						break;
				}
				if (doActions)
				{
					for (int j = rule.actions.length - 1; j >= 0; j--)
					{
						actions.addFirst(rule.getAction(j));
					}
				}
			}
		}
	}

    @Override
    public void run()
    {
        try
        {
			status = ConnectStatus.CONNECTING;
			robot.connectToRobot(btAddress);
			status = ConnectStatus.CONNECTED;
			float disInput;
			float lightInput;
			while (status == ConnectStatus.CONNECTED)
			{
				disInput = robot.getuSensor().getSample();
				lightInput = robot.getRGBSensor().getSample();
				distance = "Distance is " + disInput;
				light = "Light is " + lightInput;

				updateBeliefs(disInput, lightInput);
				checkRules();

				while(actions.peek() != null)
				{
					switch (actions.poll())
					{
						case FORWARD:
							robot.forward();
							break;
						case FORWARD_A_BIT:
							robot.short_forward();
							break;
						case STOP:
							robot.stop();
							break;
						case BACK_A_BIT:
							robot.short_backward();
							break;
						case BACKWORD:
							robot.backward();
							break;
						case LEFT:
							robot.left();
							break;
						case LEFT_A_BIT:
							robot.short_left();
							break;
						case RIGHT:
							robot.right();
							break;
						case RIGHT_A_BIT:
							robot.short_right();
							break;
						case SCARE:
							robot.scare();
							break;
					}
				}
			}

			robot.close();
			status = ConnectStatus.DISCONNECTED;

        }
        catch (Exception e)
        {
			status = ConnectStatus.DISCONNECTING;
			if (robot != null && robot.isConnected())
			{
				robot.close();
			}
			status = ConnectStatus.DISCONNECTED;
            generatedException = e;
        }
    }

	public BluetoothRobot()
	{
		actions = new LinkedBlockingDeque<RobotActions>();

		rules = new RobotRules[]{
				new RobotRules(),
				new RobotRules(),
				new RobotRules()
		};

		beliefs = new StringBuilder("Beliefs - []");
		robot = new Robot();
	}

	public void addAction(RobotActions action)
	{
		actions.add(action);
	}

	public RobotRules[] getAllRules()
	{
		return rules;
	}

    public Exception getGeneratedException()
    {
        return generatedException;
    }

	public void setBTAddress(String address)
	{
		btAddress = address;
	}

	public ConnectStatus connectionStatus()
	{
		return status;
	}

	public void disconnect()
	{
		status = ConnectStatus.DISCONNECTING;
	}

	public void close()
	{
		if (robot != null)
		{
			robot.close();
		}
	}

	public String getBeliefString()
	{
		return beliefs.toString();
	}

	public String getDistance()
	{
		return distance;
	}

	public String getLight()
	{
		return light;
	}

	public String getMessages()
	{
		if (robot != null)
		{
			return robot.getMessages();
		}
		else
		{
			return "No Current Messages";
		}
	}

	public int getStepNo()
	{
		if (robot != null)
		{
			return robot.getStepNo();
		}
		else
		{
			return 0;
		}
	}

	public void changeSettings(float objectRange, float waterLower, float waterUpper, float pathRange)
	{
		objectDetected = objectRange;
		waterLightRange = new PointF(waterLower, waterUpper);
		pathLight = pathRange;
	}
}
