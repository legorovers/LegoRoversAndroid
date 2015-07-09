package Actions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joecollenette.legorovers.BluetoothRobot;
import com.example.joecollenette.legorovers.R;

import java.util.List;
import java.util.Objects;

/**
 * Created by joecollenette on 08/07/2015.
 */
public class ActionListAdapter extends BaseAdapter
{
	private class ViewItems
	{
		public TextView txtAction;
		public ImageButton cmdDel;
	}
	protected List<BluetoothRobot.RobotActions> actions;

	private Context context;
	private LayoutInflater layoutInflater;

	public ActionListAdapter(Context _context, List<BluetoothRobot.RobotActions> _actions)
	{
		actions = _actions;
		layoutInflater = LayoutInflater.from(_context);
		context = _context;
	}

	@Override
	public int getCount()
	{
		return actions.size();
	}

	@Override
	public Object getItem(int position)
	{
		return actions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewItems holder;
		if (convertView == null) {

			holder = new ViewItems();
			convertView = layoutInflater.inflate(R.layout.action_item_layout,
					parent, false);

			holder.txtAction = (TextView)convertView.findViewById(R.id.txtAction);

			convertView.setTag(holder);
		} else {
			holder = (ViewItems) convertView.getTag();
		}

		BluetoothRobot.RobotActions action = actions.get(position);
		holder.txtAction.setText(action.toString());

		return convertView;
	}

}
