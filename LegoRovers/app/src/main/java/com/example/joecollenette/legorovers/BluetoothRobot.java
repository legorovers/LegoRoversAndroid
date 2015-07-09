package com.example.joecollenette.legorovers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.PointF;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by joecollenette on 02/07/2015.
 */
public class BluetoothRobot implements Runnable
{

	private static RobotActions[] a = RobotActions.values();

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

    private BluetoothAdapter btAdapter;
	private BluetoothDevice robotDevice;
    private Robot robot;
    private Exception generatedException;
	private String btAddress;
	private boolean connected = false;
	private ConcurrentLinkedQueue<RobotActions> actions;

	private String distance = "Distance is -";
	private String light = "Light is -";
	private RobotRules[] rules;

	private boolean path;
	private boolean water;
	private boolean obstacle;
	private StringBuilder beliefs = new StringBuilder();

	private Float objectDetected = 0.4f;
	private Float pathLight = 0.09f;
	private PointF waterLightRange = new PointF(0.06f, 0.09f) ;

	private void updateBeliefs(float distance, float light)
	{
		obstacle = distance < objectDetected;
		path = light > pathLight;
		water = light > waterLightRange.x && light < waterLightRange.y;

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

    @Override
    public void run()
    {
        try
        {
            if (robot == null)
			{
				robot = new Robot(btAddress);
				robot.connect();
			}

			connected = true;
			float disInput;
			float lightInput;
			while (connected)
			{
				disInput = robot.getuSensor().getSample();
				lightInput = robot.getRGBSensor().getSample();
				distance = "Distance is " + disInput;
				light = "Light is " + disInput;

				updateBeliefs(disInput, lightInput);

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

        }
        catch (Exception e)
        {
			if (robot != null)
			{
				robot.close();
			}
            generatedException = e;
        }
    }

	public BluetoothRobot()
	{
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		actions = new ConcurrentLinkedQueue<RobotActions>();

		rules = new RobotRules[]{
				new RobotRules(),
				new RobotRules(),
				new RobotRules()
		};
	}

	public void addAction(RobotActions action)
	{
		actions.add(action);
	}

	public void editRule(int pos, RobotRules rule)
	{
		rules[pos] = rule;
	}

	public RobotRules getRule(int pos)
	{
		return rules[pos];
	}

	public RobotRules[] getAllRules()
	{
		return rules;
	}

    public Exception getGeneratedException()
    {
        return generatedException;
    }

    public Set<BluetoothDevice> getPairedDevices()
    {
        return btAdapter.getBondedDevices();
    }

	public void setBTAddress(String address)
	{
		btAddress = address;
	}

	public BluetoothAdapter getBluetoothAdapter()
	{
		return btAdapter;
	}

	public boolean isConnected()
	{
		return connected;
	}

	public void disconnect()
	{
		connected = false;
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

	public int getPercentConnected()
	{
		if (robot != null)
		{
			return robot.getPercentConnected();
		}
		else
		{
			return 0;
		}
	}
}
