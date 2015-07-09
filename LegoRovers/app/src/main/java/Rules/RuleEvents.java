package Rules;

import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.joecollenette.legorovers.BluetoothRobot;
import com.example.joecollenette.legorovers.LayoutManager;
import com.example.joecollenette.legorovers.R;

/**
 * Created by joecollenette on 09/07/2015.
 */
public class RuleEvents
{
	private LinearLayout ruleLayout;
	private BluetoothRobot.RobotRules[] rules;

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

	public RuleEvents(LinearLayout _ruleLayout, BluetoothRobot.RobotRules[] _rules)
	{
		ruleLayout = _ruleLayout;
		rules = _rules;
	}
}
