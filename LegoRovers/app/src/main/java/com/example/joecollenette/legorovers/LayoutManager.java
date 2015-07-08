package com.example.joecollenette.legorovers;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by joecollenette on 03/07/2015.
 */
public final class LayoutManager
{
	private static PrintStream colourStream;
	private static final int[] layout_position_id = new int[]{
			R.id.rover,
			R.id.rules,
			R.id.action,
			R.id.settings
	};

	//Prevent instances of class being called
	private LayoutManager()
	{

	}

	public static int getVisibility(int position, int layout_id)
	{
		if (layout_position_id[position] == layout_id)
		{
			return View.VISIBLE;
		}
		else
		{
			return View.INVISIBLE;
		}
	}

	public static void setUpRulesView()
	{

	}

	public static void setUpSettingsView(LinearLayout settingsView, boolean connected, Resources resources)
	{
		//btRobot.stopValueWatch();

		String[] btAddress = resources.getStringArray(R.array.bluetooth_address);
		((TextView)settingsView.findViewById(R.id.txtBT1)).setText(btAddress[0]);
		((TextView)settingsView.findViewById(R.id.txtBT2)).setText(btAddress[1]);
		((TextView)settingsView.findViewById(R.id.txtBT3)).setText(btAddress[2]);
		((TextView)settingsView.findViewById(R.id.txtBT4)).setText(btAddress[3]);


		if (connected)
		{
			((TextView) settingsView.findViewById(R.id.lblStatus)).setText("Connected");
			((Button) settingsView.findViewById(R.id.cmdConnect)).setText("Disconnect");
			((TextView) settingsView.findViewById(R.id.txtMessages)).setText("");
		}
		else
		{
			((TextView) settingsView.findViewById(R.id.lblStatus)).setText("Not Connected");
			((Button) settingsView.findViewById(R.id.cmdConnect)).setText("Connect");
			((TextView) settingsView.findViewById(R.id.txtMessages)).setText(resources.getString(R.string.connect_warning));
		}
	}


}
