package edu.jcu.cs470.togenda;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

public class DayAgenda extends Activity {

	TaskCreator taskCreator;
	EventCard eventCard;
	DBAdapter db;

	private static final int MiliSecDay = 86400000;	//Number of Milliseconds in a day.
	private Cursor mCursor = null;
	private static final String[] COLS = new String[]{ CalendarContract.Instances.EVENT_ID, 
		CalendarContract.Instances.TITLE,  CalendarContract.Events.DESCRIPTION, CalendarContract.Instances.START_DAY, 
		CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.END_MINUTE, 
		CalendarContract.Instances.EVENT_COLOR_KEY, CalendarContract.Events.CALENDAR_COLOR_KEY, CalendarContract.Instances.EVENT_COLOR, 
		CalendarContract.Events.ALL_DAY};
	
	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_agenda);
		
		getActionBar().setIcon(R.drawable.ic_date);
		

		Intent i= getIntent();
		Long ldate = i.getLongExtra("longdate", 0);

		String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(ldate));
		
		getActionBar().setTitle(dateString);

//		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.setTitle(dateString);
//		LayoutInflater inflater = this.getLayoutInflater();

		// init CardView
		CardUI CardView = (CardUI) findViewById(R.id.cardsviewday);
		CardView.setSwipeable(false);
		
		Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(eventsUriBuilder, ldate); //start time = now
		ContentUris.appendId(eventsUriBuilder, ldate+(MiliSecDay));//End time = (now + 1 week)
		Uri eventsUri = eventsUriBuilder.build();

		//Fill cursor with desired calendar events.
		mCursor = this.getContentResolver().query(eventsUri, COLS, null, null, ldate + " ASC");
		mCursor.moveToFirst();

		ArrayList<CardTemplate> cardList = new ArrayList<CardTemplate>();

		boolean makeCards = true;

		CardStack stack = new CardStack();

		while(makeCards)
		{		
			EventCard newCard = getEvent();
			if (newCard.AllDay == true)
			{
				if (newCard.startTime <= ldate)
				{
					stack.add(newCard);
				}
			}
			else if (newCard.getTitle() != "no event") //"no event" == try-catch block
			{
				cardList.add(newCard);
			}
			else
			{
				makeCards = false; //do not add card
			}
			if (newCard.isLast() == true)
			{
				makeCards = false;
			}
		}

		Collections.sort(cardList);

		CardView.addStack(stack);

		if (!cardList.isEmpty())
		{
			//Stacked cards are kind of awkward to use, and when placed in excession they cause lag.
			//Ordinary events will no longer be stacked. Instead, only full-day events will be stacked with each other when
			//multiple full-day events exist on the same day.
			//full day events aren't properly implemented yet.
			//Will use a different card format for full day events, as well as tasks so that different types of entries are
			//easily identified.

			for (int cards = cardList.size(); cards >= 1; cards--)
			{
				CardView.addCard(cardList.get(cards-1));
			}
		}

		if (cardList.isEmpty())
		{
			//Create cardtype that explains that there are no current events.
		}

		// draw cards
		CardView.refresh();
		
	}
	
	public EventCard getEvent() {
		try
		{
			EventCard event;
			String title;
			long start;
			long end;
			//			int color; //holds custom color case
			String colorKey, colorKey2;
			String desc;
			String eventId;
			boolean last = false;
			boolean allday = false;

			try {

				//				CalendarContract.Instances.EVENT_ID,
				eventId = mCursor.getString(0);
				//				CalendarContract.Instances.TITLE,
				title = mCursor.getString(1);
				//				CalendarContract.Events.DESCRIPTION, 
				desc = mCursor.getString(2);
				//				CalendarContract.Instances.START_DAY,

				//				CalendarContract.Instances.START_MINUTE,
				start = mCursor.getLong(4);					//MIGHT REQUIRE LONG
				//				CalendarContract.Instances.END_DAY,

				//				CalendarContract.Instances.END_MINUTE,
				end = mCursor.getLong(5);					//MIGHT REQUIRE LONG
				//				CalendarContract.Instances.EVENT_COLOR_KEY,
				colorKey = mCursor.getString(7);
				colorKey2 = mCursor.getString(8);
				//				CalendarContract.Instances.EVENT_COLOR};

				if (mCursor.getInt(10) == 1)
				{
					allday = true;
				}

			} catch (Exception e) {
				//ignore
				return new EventCard("no event");
			}

			if(mCursor.isLast()) 
			{
				last = true;
			}
			else
			{
				mCursor.moveToNext();
			}

			//creates event (title,description,star time,end time,event color, calendar color,is clickable,have overflow button,id,is last)
			event = new EventCard(title, desc, start, end, colorKey, colorKey2, true, true, eventId, last, allday);

			return event;
		}
		catch(Exception ex)
		{
			return new EventCard("no event");
		}
	}
	
}
