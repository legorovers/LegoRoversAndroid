package com.example.joecollenette.legorovers;

import android.app.ActionBar;
import android.content.ClipData;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import Actions.ActionDragListener;
import Actions.ButtonShadow;
import Rules.RuleEvents;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private boolean viewCreated = false;
    private BluetoothRobot btRobot;
	private Thread robotThread;
	private Handler connectHandle;
	private Handler dataHandle;
	private RuleEvents rEvents;


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

	private Runnable connectUpdate = new Runnable()
	{
		@Override
		public void run()
		{
			((TextView)findViewById(R.id.txtMessages)).setText(btRobot.getMessages());
			((ProgressBar)findViewById(R.id.pgbStatus)).setProgress(btRobot.getPercentConnected() * 100);

			if (btRobot.getPercentConnected() == 1 || btRobot.getGeneratedException() != null)
			{
				findViewById(R.id.cmdConnect).setEnabled(true);
				findViewById(R.id.drawer_layout).setEnabled(true);
				if (btRobot.getGeneratedException() != null)
				{
					((TextView)findViewById(R.id.txtMessages)).append('\n' + btRobot.getGeneratedException().getMessage());
					((Button)findViewById(R.id.cmdConnect)).setText("Connect");
				}
				else
				{
					((TextView)findViewById(R.id.txtMessages)).append("\nConnected");
					((Button)findViewById(R.id.cmdConnect)).setText("Disconnect");
				}
				connectHandle.removeCallbacks(connectUpdate);
			}
			else
			{
				connectHandle.postDelayed(connectUpdate, 100);
			}
		}
	};

	public void swtRuleChanged(View view)
	{

	}

	public void cmdActionClicked(View view)
	{
		if (btRobot.isConnected())
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

	public void cmdConnectClicked(View view)
	{
		if (!btRobot.isConnected())
		{
			btRobot.setBTAddress(String.format("%s.%s.%s.%s", getResources().getStringArray(R.array.bluetooth_address)[0]
					, getResources().getStringArray(R.array.bluetooth_address)[1]
					, getResources().getStringArray(R.array.bluetooth_address)[2]
					, getResources().getStringArray(R.array.bluetooth_address)[3]));

			if (robotThread == null)
			{
				robotThread = new Thread(btRobot);
				robotThread.start();
			}
			else
			{
				btRobot.close();
				robotThread = new Thread(btRobot);
				robotThread.start();
			}
		}
		else
		{
			if (robotThread != null)
			{
				btRobot.disconnect();
			}
			else
			{
				btRobot.close();
			}
		}
		findViewById(R.id.cmdConnect).setEnabled(false);
		((ProgressBar)findViewById(R.id.pgbStatus)).setProgress(0);
		findViewById(R.id.drawer_layout).setEnabled(false);
		connectHandle.post(connectUpdate);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		btRobot = new BluetoothRobot();

		connectHandle = new Handler();
		dataHandle = new Handler();
        viewCreated = true;
        onNavigationDrawerItemSelected(0);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getResources().getStringArray(R.array.menu_options)[0];

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));



		if (savedInstanceState != null)
		{
			onNavigationDrawerItemSelected(savedInstanceState.getInt("curPos"));
		}

		rEvents = new RuleEvents((LinearLayout)findViewById(R.id.rules), btRobot.getAllRules());
		((Spinner)findViewById(R.id.cboRule)).setOnItemSelectedListener(rEvents.new ruleChanged());
		((Switch)findViewById(R.id.swtRule)).setOnCheckedChangeListener(rEvents.new swtChanged());
		((Spinner)findViewById(R.id.cboObstacle)).setOnItemSelectedListener(rEvents.new obstacleChange());
		((Spinner)findViewById(R.id.cboAction1)).setOnItemSelectedListener(rEvents.new actionChanged());
		((Spinner)findViewById(R.id.cboAction2)).setOnItemSelectedListener(rEvents.new actionChanged());
		((Spinner)findViewById(R.id.cboAction3)).setOnItemSelectedListener(rEvents.new actionChanged());


		((ListView)findViewById(R.id.lstActions)).setOnDragListener(new ActionDragListener());
		((Button)findViewById(R.id.cmdAFor_A_Bit)).setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View view)
			{
				ClipData clipData = ClipData.newPlainText("Action", "FAB");
				View.DragShadowBuilder shadow = new ButtonShadow(((Button) findViewById(R.id.cmdAFor_A_Bit)));

				view.startDrag(clipData, shadow, BluetoothRobot.RobotActions.FORWARD_A_BIT, 0);
				return true;
			}
		});
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
			findViewById(R.id.action).setVisibility(LayoutManager.getVisibility(position, R.id.action));
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
                case 3:
                    LayoutManager.setUpSettingsView((LinearLayout) findViewById(R.id.settings),
                            btRobot.isConnected(), getResources());
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
