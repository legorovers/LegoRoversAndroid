package Managers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.joecollenette.legorovers.BluetoothRobot;
import com.example.joecollenette.legorovers.R;

import java.io.PrintStream;

/**
 * Created by joecollenette on 03/07/2015.
 */
public final class LayoutManager
{
	private static final int[] layout_position_id = new int[]{
			R.id.rover,
			R.id.rules,
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
	
	private static void setResizedParam(Button id, int size, Activity view)
	{
		TableRow.LayoutParams param = (TableRow.LayoutParams)(id).getLayoutParams();
		param.height = size;
		param.width = size;
		id.setLayoutParams(param);
	}

	public static void setUpManualView(Activity manualView)
	{
		Configuration configuration = manualView.getResources().getConfiguration();
		Point size = new Point();
		manualView.getWindowManager().getDefaultDisplay().getSize(size);

		int button_width = size.x / 5;
		int button_height = size.y / 5;

		int smallest = button_width < button_height ? button_width : button_height;
		setResizedParam((Button)manualView.findViewById(R.id.cmdForward), smallest, manualView);
		setResizedParam((Button)manualView.findViewById(R.id.cmdFor_A_Bit), smallest, manualView);
		setResizedParam((Button)manualView.findViewById(R.id.cmdStop), smallest, manualView);
		setResizedParam((Button)manualView.findViewById(R.id.cmdBack_A_Bit), smallest, manualView);
		setResizedParam((Button)manualView.findViewById(R.id.cmdBack), smallest, manualView);
		setResizedParam((Button)manualView.findViewById(R.id.cmdLeft), smallest, manualView);
		setResizedParam((Button)manualView.findViewById(R.id.cmdLeft_A_Bit), smallest, manualView);
		setResizedParam((Button)manualView.findViewById(R.id.cmdRight_A_Bit), smallest, manualView);
		setResizedParam((Button)manualView.findViewById(R.id.cmdRight), smallest, manualView);

	}

	public static void setUpRulesView(LinearLayout rulesView, BluetoothRobot.RobotRules[] rules)
	{
		int pos = ((Spinner)rulesView.findViewById(R.id.cboRule)).getSelectedItemPosition();

		BluetoothRobot.RobotRules rule = rules[pos];

		((Switch)rulesView.findViewById(R.id.swtRule)).setChecked(rule.getEnabled());
		((Spinner)rulesView.findViewById(R.id.cboObstacle)).setSelection(rule.getOnAppeared());

		((Spinner)rulesView.findViewById(R.id.cboAction1)).setSelection(rule.getAction(0).toInt());
		((Spinner)rulesView.findViewById(R.id.cboAction2)).setSelection(rule.getAction(1).toInt());
		((Spinner)rulesView.findViewById(R.id.cboAction3)).setSelection(rule.getAction(2).toInt());

	}

	public static void setUpSettingsView(LinearLayout settingsView, boolean connected, Resources resources, SharedPreferences prefs)
	{
		((TextView)settingsView.findViewById(R.id.txtBT1)).setText(String.valueOf(prefs.getInt("BT1", 10)));
		((TextView)settingsView.findViewById(R.id.txtBT2)).setText(String.valueOf(prefs.getInt("BT2", 0)));
		((TextView)settingsView.findViewById(R.id.txtBT3)).setText(String.valueOf(prefs.getInt("BT3", 1)));
		((TextView)settingsView.findViewById(R.id.txtBT4)).setText(String.valueOf(prefs.getInt("BT4", 1)));


		if (connected)
		{
			((Button) settingsView.findViewById(R.id.cmdConnect)).setText("Disconnect");
			((TextView) settingsView.findViewById(R.id.txtMessages)).setText("");
		}
		else
		{
			((Button) settingsView.findViewById(R.id.cmdConnect)).setText("Connect");
			((TextView) settingsView.findViewById(R.id.txtMessages)).setText(resources.getString(R.string.connect_warning));
		}
	}


}
