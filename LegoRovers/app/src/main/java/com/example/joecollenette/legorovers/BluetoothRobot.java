package com.example.joecollenette.legorovers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.TextView;
import android.os.Handler;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.LogRecord;

import EV3.BasicRobot;
import EV3.EASSEV3Environment;
import EV3.EASSRGBColorSensor;
import EV3.EASSSensor;

/**
 * Created by joecollenette on 02/07/2015.
 */
public class BluetoothRobot implements Runnable
{
	public enum RobotActions
	{
		FORWARD,
		FORWARD_A_BIT,
		STOP,
		BACK_A_BIT,
		BACKWORD,
		LEFT,
		RIGHT,
		SCARE
	}

    private BluetoothAdapter btAdapter;
	private BluetoothDevice robotDevice;
    private Robot robot;
    private Exception generatedException;
	private String btAddress;
	private boolean connected = false;
	private ConcurrentLinkedQueue<RobotActions> actions;

	private String distanceInput;
	private StringBuilder colourInput;

	private boolean doUpdates;


    @Override
    public void run()
    {
        try
        {
            if (robot == null)
			{
				robot = new Robot(btAddress);
				robot.connect();
				colourInput = new StringBuilder();

			}

			connected = true;


			robot.getRGBSensor().setPrintStream(new PrintStream(new OutputStream()
			{
				@Override
				public void write(int oneByte) throws IOException
				{
					if (oneByte == '\n')
					{
						colourInput.setLength(0);
					}
					else
					{
						colourInput.append(oneByte);
					}
				}
			}));


			while (connected)
			{
				if (doUpdates)
				{
					distanceInput = "Distance is " + robot.getuSensor().getSample();
				}

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
						case RIGHT:
							robot.right();
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
		doUpdates = false;
	}

	public void addAction(RobotActions action)
	{
		actions.add(action);
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

	public void monitorValues(Boolean update)
	{
		doUpdates = update;
	}

	public String getDistance()
	{
		return distanceInput;
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
