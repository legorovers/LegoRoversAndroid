package Actions;

import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by joecollenette on 08/07/2015.
 */
public class ActionDragListener implements View.OnDragListener
{
	@Override
	public boolean onDrag(View view, DragEvent dragEvent)
	{
		boolean valid = false;
		switch (dragEvent.getAction())
		{
			case DragEvent.ACTION_DRAG_STARTED:
				if (dragEvent.getClipDescription().getLabel().toString().equals("Action"))
				{
					valid = true;
					view.setBackgroundColor(Color.BLUE);
					view.invalidate();

				}
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				valid = true;
				view.setBackgroundColor(Color.GREEN);
				view.invalidate();
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				view.setBackgroundColor(Color.GRAY);
				view.invalidate();
				valid = true;
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				view.setBackgroundColor(Color.BLUE);
				view.invalidate();
				valid = true;
				break;
			case DragEvent.ACTION_DRAG_LOCATION:
				valid = true;
				break;
			case DragEvent.ACTION_DROP:
				view.setBackgroundColor(Color.RED);
				view.invalidate();
				valid = true;
		}
		return valid;
	}
}
