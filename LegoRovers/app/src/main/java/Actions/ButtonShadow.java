package Actions;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

/**
 * Created by joecollenette on 08/07/2015.
 */
public class ButtonShadow extends View.DragShadowBuilder
{
	private static Drawable shadow;

	public ButtonShadow(Button v)
	{
		super(v);

		shadow = new ColorDrawable(Color.LTGRAY);
	}

	@Override
	public void onProvideShadowMetrics(Point size, Point touch)
	{
		int width = getView().getWidth() - 10;
		shadow.setBounds(0, 0, width, width);

		size.set(width, width);
		touch.set(width / 2, width / 2);
	}

	@Override
	public void onDrawShadow(Canvas canvas)
	{
		shadow.draw(canvas);
	}
}
