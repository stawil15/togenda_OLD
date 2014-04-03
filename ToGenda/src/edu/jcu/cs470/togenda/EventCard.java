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

public class EventCard extends RecyclableCard implements Comparable{

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

			if (Integer.parseInt(color) < 25 && Integer.parseInt(color) > 0) //Later: compensate for custom colors
			{

				colors = new int[25];

				colors[1] = R.color.gCal1;
				colors[2] = R.color.gCal2;
				colors[3] = R.color.gCal3;
				colors[4] = R.color.gCal4;
				colors[5] = R.color.gCal5;
				colors[6] = R.color.gCal6;
				colors[7] = R.color.gCal7;
				colors[8] = R.color.gCal8;
				colors[9] = R.color.gCal9;
				colors[10] = R.color.gCal10;
				colors[11] = R.color.gCal11;
				colors[12] = R.color.gCal12;
				colors[13] = R.color.gCal13;
				colors[14] = R.color.gCal14;
				colors[15] = R.color.gCal15;
				colors[16] = R.color.gCal16;
				colors[17] = R.color.gCal17;
				colors[18] = R.color.gCal18;
				colors[19] = R.color.gCal19;
				colors[20] = R.color.gCal20;
				colors[21] = R.color.gCal21;
				colors[22] = R.color.gCal22;
				colors[23] = R.color.gCal23;
				colors[24] = R.color.gCal24;

				((LinearLayout) convertView.findViewById(R.id.background)).setBackgroundResource(colors[Integer.parseInt(color)]);
			}

		}

	}

	@Override
	public void setOnCardSwipedListener(OnCardSwiped onEpisodeSwipedListener) {
		this.onCardSwipedListener = onEpisodeSwipedListener;
	}

	public EventCard(String titlePlay, String description, long start2, long end2, String color, String color2, Boolean hasOverflow, Boolean isClickable, String eventId, boolean last) {
		//super(titlePlay, description, color, color, hasOverflow, isClickable);
		this.titlePlay = titlePlay;
		this.description = description;
		
		//FIX TIME
		//SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
		SimpleDateFormat _12HourSDFwDM = new SimpleDateFormat("d/M h:mm a");
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("h:mm a");
		this.start = _12HourSDFwDM.format(new Time(start2));
		this.end = _12HourSDF.format(new Time(end2));
		
		if (color != "" && color != null)
		{
			this.color = color;
		}
		else
		{
			this.color = color2;
		}
		this.hasOverflow = hasOverflow;
		this.isClickable = isClickable;
		this.eventId = eventId;
		this.last = last;

	}

//SET onClick EVENT TO GO TO GOOGLE CAL
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
	
	public long getStart()
	{
		return Integer.parseInt(start);
	}

	public long getEnd()
	{
		return Integer.parseInt(end);
	}

	public int compareTo(Object another) {
		if (this.getStart() < ((EventCard) another).getStart())
		{
			return 1;
		}
//		else if (Long.parseLong(start) > ((EventCard) another).getStart())
//		{
//			return -1;
//		}
		return 0;
	}
}
