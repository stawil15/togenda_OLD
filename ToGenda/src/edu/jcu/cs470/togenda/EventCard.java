package edu.jcu.cs470.togenda;

import java.sql.Time;
import java.text.SimpleDateFormat;

import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

public class EventCard extends RecyclableCard {

	String start;
	String end;
	boolean last = false;

	int[] colors;
	private OnCardSwiped onCardSwipedListener;
	private String eventId = "0";

	public EventCard(String title){
		super(title);
	}

	@Override
	protected int getCardLayoutId() {
		return R.layout.card_ex;
	}

	@Override
	protected void applyTo(View convertView) {
		((TextView) convertView.findViewById(R.id.EventLabel)).setText(titlePlay);
		((TextView) convertView.findViewById(R.id.description)).setText(description);
		((TextView) convertView.findViewById(R.id.Time)).setText(start+" - "+end);

		if (color!=null){

			if (Integer.parseInt(color) < 25 && Integer.parseInt(color) > 0)
			{

				colors = new int[25];

				colors[1] = R.color.Brown;
				colors[2] = R.color.Maroon;
				colors[3] = R.color.Tomato;
				colors[4] = R.color.Red;
				colors[5] = R.color.OrangeRed;
				colors[6] = R.color.Orange;
				colors[7] = R.color.SeaGreen;
				colors[8] = R.color.DarkGreen;
				colors[9] = R.color.Green;
				colors[10] = R.color.YellowGreen;
				colors[11] = R.color.LightGoldenrodYellow;
				colors[12] = R.color.Yellow;
				colors[13] = R.color.LightGreen;
				colors[14] = R.color.LightCoral;
				colors[15] = R.color.LightBlue;
				colors[16] = R.color.Blue;
				colors[17] = R.color.BlueViolet;
				colors[18] = R.color.Azure;
				colors[19] = R.color.Gray;
				colors[20] = R.color.Beige;
				colors[21] = R.color.SaddleBrown;
				colors[22] = R.color.Pink;
				colors[23] = R.color.Violet;
				colors[24] = R.color.Purple;

				((LinearLayout) convertView.findViewById(R.id.background)).setBackgroundResource(colors[Integer.parseInt(color)]);
			}

		}

	}

	@Override
	public void setOnCardSwipedListener(OnCardSwiped onEpisodeSwipedListener) {
		this.onCardSwipedListener = onEpisodeSwipedListener;
	}

	public EventCard(String titlePlay, String description, Long start, Long end, String color, Boolean hasOverflow, Boolean isClickable, String eventId, boolean last) {
		//super(titlePlay, description, color, color, hasOverflow, isClickable);
		this.titlePlay = titlePlay;
		this.description = description;
		//SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("h:mm a");
		this.start = _12HourSDF.format(new Time(start));
		this.end = _12HourSDF.format(new Time(end));
		this.color = color;
		this.hasOverflow = hasOverflow;
		this.isClickable = isClickable;
		this.eventId = eventId;
		this.last = last;

	}


//	public void onClick(View v) {
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		//Android 2.2+
//		intent.setData(Uri.parse("content://com.android.calendar/events/" + eventId));  
//		//Android 2.1 and below.
//		//intent.setData(Uri.parse("content://calendar/events/" + String.valueOf(calendarEventID)));    
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//				| Intent.FLAG_ACTIVITY_SINGLE_TOP
//				| Intent.FLAG_ACTIVITY_CLEAR_TOP
//				| Intent.FLAG_ACTIVITY_NO_HISTORY
//				| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//		v.getContext().startActivity(intent);
//
//	}
	
	public void setDescription(String desc)
	{
		this.description = desc;
	}
	
	public boolean isLast()
	{
		return last;
	}

}
