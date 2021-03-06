package com.example.joecollenette.legorovers;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import Managers.EventManager;
import Managers.LayoutManager;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private boolean viewCreated = false;
    private BluetoothRobot btRobot;
	private Thread robotThread;
	private Handler connectHandle;
	private Handler disconnectHandle;
	private Handler dataHandle;
	private EventManager rEvents;
	private SharedPreferences prefs;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

	private Runnable dataUpdate = new Runnable()
	{
		@Override
		public void run()
		{
			((TextView)findViewById(R.id.txtDistance)).setText(btRobot.getDistance());
			((TextView)findViewById(R.id.txtColour)).setText(btRobot.getLight());
			((TextView)findViewById(R.id.txtBeliefs)).setText(btRobot.getBeliefString());
			connectHandle.postDelayed(dataUpdate, 100);
		}
	};

	private Runnable disconnectUpdate = new Runnable()
	{
		@Override
		public void run()
		{
			((TextView)findViewById(R.id.txtMessages)).setText(btRobot.getMessages());
			((ProgressBar)findViewById(R.id.pgbStatus)).setProgress(btRobot.getStepNo());

			if (btRobot.getGeneratedException() != null)
			{
				((TextView)findViewById(R.id.txtMessages)).append('\n' + btRobot.getGeneratedException().getMessage());
				findViewById(R.id.cmdConnect).setEnabled(true);
				findViewById(R.id.drawer_layout).setEnabled(true);
				disconnectHandle.removeCallbacks(disconnectUpdate);
			}
			else if (btRobot.connectionStatus() == BluetoothRobot.ConnectStatus.DISCONNECTED)
			{
				((TextView)findViewById(R.id.txtMessages)).append("\nDisconnected");
				((Button)findViewById(R.id.cmdConnect)).setText("Connect");
				findViewById(R.id.cmdConnect).setEnabled(true);
				findViewById(R.id.drawer_layout).setEnabled(true);
				disconnectHandle.removeCallbacks(disconnectUpdate);
			}
			else
			{
				disconnectHandle.postDelayed(disconnectUpdate, 100);
			}
		}
	};

	private Runnable connectUpdate = new Runnable()
	{
		@Override
		public void run()
		{
			((TextView)findViewById(R.id.txtMessages)).setText(btRobot.getMessages());
			((ProgressBar)findViewById(R.id.pgbStatus)).setProgress(btRobot.getStepNo());

			if (btRobot.getGeneratedException() != null)
			{
				((TextView)findViewById(R.id.txtMessages)).append('\n' + btRobot.getGeneratedException().getMessage());
				findViewById(R.id.cmdConnect).setEnabled(true);
				findViewById(R.id.drawer_layout).setEnabled(true);
				connectHandle.removeCallbacks(connectUpdate);
			}
			else if (btRobot.connectionStatus() == BluetoothRobot.ConnectStatus.CONNECTED)
			{
				((TextView)findViewById(R.id.txtMessages)).append("\nConnected");
				((Button)findViewById(R.id.cmdConnect)).setText("Disconnect");
				findViewById(R.id.cmdConnect).setEnabled(true);
				findViewById(R.id.drawer_layout).setEnabled(true);
				connectHandle.removeCallbacks(connectUpdate);
			}
			else
			{
				connectHandle.postDelayed(connectUpdate, 100);
			}
		}
	};

	public void cmdActionClicked(View view)
	{
		if (btRobot.connectionStatus() == BluetoothRobot.ConnectStatus.CONNECTED)
		{
			switch (view.getId())
			{
				case R.id.cmdForward:
					btRobot.addAction(BluetoothRobot.RobotActions.FORWARD);
					break;
				case R.id.cmdFor_A_Bit:
					btRobot.addAction(BluetoothRobot.RobotActions.FORWARD_A_BIT);
					break;
				case R.id.cmdStop:
					btRobot.addAction(BluetoothRobot.RobotActions.STOP);
					break;
				case R.id.cmdBack_A_Bit:
					btRobot.addAction(BluetoothRobot.RobotActions.BACK_A_BIT);
					break;
				case R.id.cmdBack:
					btRobot.addAction(BluetoothRobot.RobotActions.BACKWORD);
					break;
				case R.id.cmdLeft:
					btRobot.addAction(BluetoothRobot.RobotActions.LEFT);
					break;
				case R.id.cmdLeft_A_Bit:
					btRobot.addAction(BluetoothRobot.RobotActions.LEFT_A_BIT);
					break;
				case R.id.cmdRight_A_Bit:
					btRobot.addAction(BluetoothRobot.RobotActions.RIGHT_A_BIT);
					break;
				case R.id.cmdRight:
					btRobot.addAction(BluetoothRobot.RobotActions.RIGHT);
					break;
				case R.id.cmdScare:
					btRobot.addAction(BluetoothRobot.RobotActions.SCARE);
					break;
			}
		}
	}

	public void cmdSetDetection(View view)
	{

		btRobot.changeSettings(
				Float.parseFloat(((TextView)findViewById(R.id.txtObs)).getText().toString()),
				Float.parseFloat(((TextView) findViewById(R.id.txtwLower)).getText().toString()),
				Float.parseFloat(((TextView)findViewById(R.id.txtwUpper)).getText().toString()),
				Float.parseFloat(((TextView)findViewById(R.id.txtPath)).getText().toString())
		);
	}

	public void cmdConnectClicked(View view)
	{
		findViewById(R.id.cmdConnect).setEnabled(false);
		((ProgressBar)findViewById(R.id.pgbStatus)).setProgress(0);
		findViewById(R.id.drawer_layout).setEnabled(false);
		((TextView)findViewById(R.id.txtMessages)).setText("");

		if (btRobot.connectionStatus() == BluetoothRobot.ConnectStatus.DISCONNECTED)
		{
			btRobot.setBTAddress(String.format("%d.%d.%d.%d", prefs.getInt("BT1", 10)
					, prefs.getInt("BT2", 0)
					, prefs.getInt("BT3", 1)
					, prefs.getInt("BT4", 1)));
			robotThread = new Thread(btRobot);
			robotThread.start();
			connectHandle.post(connectUpdate);
		}
		else
		{
			btRobot.disconnect();
			disconnectHandle.post(disconnectUpdate);
		}


	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		btRobot = new BluetoothRobot();
		connectHandle = new Handler();
		dataHandle = new Handler();
		disconnectHandle = new Handler();
        viewCreated = true;
        onNavigationDrawerItemSelected(0);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getResources().getStringArray(R.array.menu_options)[0];

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		if (savedInstanceState != null)
		{
			onNavigationDrawerItemSelected(savedInstanceState.getInt("curPos"));
		}

		rEvents = new EventManager((LinearLayout)findViewById(R.id.rules), btRobot.getAllRules());


		((TextView) findViewById(R.id.txtBT1)).addTextChangedListener(rEvents.new txtBTTextChanged(R.id.txtBT1, prefs));
		((TextView) findViewById(R.id.txtBT2)).addTextChangedListener(rEvents.new txtBTTextChanged(R.id.txtBT2, prefs));
		((TextView) findViewById(R.id.txtBT3)).addTextChangedListener(rEvents.new txtBTTextChanged(R.id.txtBT3, prefs));
		((TextView) findViewById(R.id.txtBT4)).addTextChangedListener(rEvents.new txtBTTextChanged(R.id.txtBT4, prefs));

		rEvents = new EventManager((LinearLayout)findViewById(R.id.rules), btRobot.getAllRules());
		((Spinner)findViewById(R.id.cboRule)).setOnItemSelectedListener(rEvents.new ruleChanged());
		((Switch)findViewById(R.id.swtRule)).setOnCheckedChangeListener(rEvents.new swtChanged());
		((Spinner)findViewById(R.id.cboObstacle)).setOnItemSelectedListener(rEvents.new obstacleChange());
		((Spinner)findViewById(R.id.cboAction1)).setOnItemSelectedListener(rEvents.new actionChanged());
		((Spinner)findViewById(R.id.cboAction2)).setOnItemSelectedListener(rEvents.new actionChanged());
		((Spinner)findViewById(R.id.cboAction3)).setOnItemSelectedListener(rEvents.new actionChanged());

    }

	@Override
	protected void onDestroy()
	{
		if (btRobot != null)
		{
			btRobot.disconnect();
		}
		super.onDestroy();
	}

    @Override @SuppressWarnings("ResourceType")
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (viewCreated) {
			mTitle = getResources().getStringArray(R.array.menu_options)[position];


			findViewById(R.id.rover).setVisibility(LayoutManager.getVisibility(position, R.id.rover)); //Ignore resource type so can return enum (int) for visibility
			findViewById(R.id.rules).setVisibility(LayoutManager.getVisibility(position, R.id.rules));
			findViewById(R.id.settings).setVisibility(LayoutManager.getVisibility(position, R.id.settings));

			dataHandle.removeCallbacks(dataUpdate);
            switch (position)
            {
                case 0:
					LayoutManager.setUpManualView(this);
                    break;
				case 1:
					dataHandle.postDelayed(dataUpdate, 100);
					LayoutManager.setUpRulesView(((LinearLayout)findViewById(R.id.rules)), btRobot.getAllRules());
					break;
                case 2:
                    LayoutManager.setUpSettingsView((LinearLayout) findViewById(R.id.settings),
                            btRobot.connectionStatus() == BluetoothRobot.ConnectStatus.CONNECTED, getResources(), prefs);
            }
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onSaveInstanceState(Bundle bundle)
	{
		bundle.putInt("curPos", mNavigationDrawerFragment.getCurrentSelectedPosition());
		super.onSaveInstanceState(bundle);
	}

}
