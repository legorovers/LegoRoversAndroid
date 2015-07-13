package Managers;

import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.joecollenette.legorovers.BluetoothRobot;
import com.example.joecollenette.legorovers.R;

/**
 * Created by joecollenette on 09/07/2015.
 */
public class EventManager
{
	private LinearLayout ruleLayout;
	private BluetoothRobot.RobotRules[] rules;

	public class txtBTTextChanged implements TextWatcher
	{
		private int viewID;
		private SharedPreferences prefs;
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
		{

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
		{

		}

		@Override
		public void afterTextChanged(Editable editable)
		{
			int entered;
			try
			{
				entered = Integer.parseInt(editable.toString());
				SharedPreferences.Editor editor = prefs.edit();
				switch (viewID)
				{
					case R.id.txtBT1:
						editor.putInt("BT1", entered);
						break;
					case R.id.txtBT2:
						editor.putInt("BT2", entered);
						break;
					case R.id.txtBT3:
						editor.putInt("BT3", entered);
						break;
					case R.id.txtBT4:
						editor.putInt("BT4", entered);
						break;
				}
				editor.commit();
			}
			catch(NumberFormatException nfe)
			{

			}
		}

		public txtBTTextChanged(int id, SharedPreferences _prefs)
		{
			viewID = id;
			prefs = _prefs;
		}
	}

	public class ruleChanged implements AdapterView.OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
		{
			LayoutManager.setUpRulesView(ruleLayout, rules);
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView)
		{

		}
	}

	public class swtChanged implements CompoundButton.OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b)
		{
			int ruleNo = ((Spinner)ruleLayout.findViewById(R.id.cboRule)).getSelectedItemPosition();

			rules[ruleNo].setEnabled(b);
		}
	}

	public class obstacleChange implements AdapterView.OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
		{
			int ruleNo = ((Spinner)ruleLayout.findViewById(R.id.cboRule)).getSelectedItemPosition();
			rules[ruleNo].setOnAppeared(i);
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView)
		{

		}
	}

	public class actionChanged implements AdapterView.OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
		{
			int ruleNo = ((Spinner)ruleLayout.findViewById(R.id.cboRule)).getSelectedItemPosition();
			BluetoothRobot.RobotActions action = BluetoothRobot.RobotActions.fromInt(i);

			int cboNum = 0;

			switch(adapterView.getId())
			{
				case R.id.cboAction1:
					cboNum = 0;
					break;
				case R.id.cboAction2:
					cboNum = 1;
					break;
				case R.id.cboAction3:
					cboNum = 2;
					break;
			}
			rules[ruleNo].editAction(action, cboNum);
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView)
		{

		}
	}

	public EventManager(LinearLayout _ruleLayout, BluetoothRobot.RobotRules[] _rules)
	{
		ruleLayout = _ruleLayout;
		rules = _rules;
	}
}
